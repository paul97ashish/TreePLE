import * as VueGoogleMaps from 'vue2-google-maps'
import Util from '../util.js'
import TreePle from '@/components/TreePle'
import Vue from 'vue'

Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyBsDIeD-bStOZuIzfV2yHAvprz_Py25sQI'
  }
})

/*class TreeDto {
 constructor(latitude, longitude, heightMeters, canopyDiameterMeters, species, land, municipality, entries, state) {
    this.latitude = latitude
    this.longitude = longitude
    this.heightMeters = heightMeters
    this.canopyDiameterMeters = canopyDiameterMeters
    this.species = species
    this.land = land
    this.municipality = municipality
    this.entries = entries
    this.state = state
 }
}*/

Number.prototype.toRadians = function() { return this * Math.PI / 180; };
Number.prototype.toDegrees = function() { return this * 180 / Math.PI; };

function distanceBetween(coords1, coords2) {
  const R = 6371e3; // metres
  const φ1 = coords1.lat.toRadians();
  const φ2 = coords2.lat.toRadians();
  const Δφ = (coords2.lat-coords1.lat).toRadians();
  const Δλ = (coords2.lng-coords1.lng).toRadians();

  const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
          Math.cos(φ1) * Math.cos(φ2) *
          Math.sin(Δλ/2) * Math.sin(Δλ/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

  return R * c;
}

async function fetchCarbonOffset(trees) {
  return (await Util.axios.post('/carbon-offset', {}, { params: { treeIds: trees.map(tree => parseInt(tree.id)), additionalProjected: 0 } })).data
}

export default {
  name: 'treeple',
  data () {
    const initialCoords = {
      lat: 45.5017156,
      lng: -73.5728669
    };
    const map = {
      zoom: 12,
      center: initialCoords,
      radiusOptions: {
        "None": "0",
        // "100 m": "0.1",
        "1 km": 1,
        // "2 km": "2",
        // "5 km": "5",
        "10 km": 10,
        "20 km": 20,
        "50 km": 50,
        "100 km": 100
      },
      circle: {
        center: initialCoords,
        radius: 0,
      }
    };
    class HeightFilter {
      constructor() {
        this.enabled = false;
        this.reset();
      }

      reset = () => { this.min = 0; this.max = 100; };

      isActive = () => this.enabled;
      clear = () => this.enabled = false;
      predicate = tree => {
        return tree.heightMeters && tree.heightMeters >= this.min && tree.heightMeters <= this.max;
      }
    }
    return {
      trees: [],
      showAllTrees: false,
      treesToShow: 5,
      errorMessage: '',
      map: map,
      filters: {
        circle: {
          isActive() { return map.circle.radius != 0; },
          clear() { map.circle.radius = 0; },
          reset() { this.clear(); },
          predicate(tree) {
            return distanceBetween(map.circle.center, {lat: tree.latitude, lng: tree.longitude}) < map.circle.radius*1000;
          }
        },
        height: new HeightFilter()
      },
      sustainability: {
        calculatingValues: true,
        carbonOffset: { all: 0, selected: 0 },
        treesToPlant: () => {
          // Note this may be out of sync until refresh
          const avgOffset = this.sustainability.carbonOffset.all / this.trees.length;

          return Math.ceil(this.sustainability.carbonTarget / avgOffset);
        },
        fetch: async () => {
          const selectedTrees = this.getFilteredTreeList()
          try {
            this.sustainability.calculatingValues = true

            this.sustainability.carbonOffset.selected = selectedTrees.length ? await fetchCarbonOffset(selectedTrees) : 0
            this.sustainability.carbonOffset.all = this.trees.length ? await fetchCarbonOffset(this.trees) : 0

            this.sustainability.calculatingValues = false
          } catch (e) {
            this.errorMessage = Util.toErrorMessage(e)
          }
        }
      },
    }
  },
  created: async function () {
    // Initializing from backend
    try {
      this.trees = (await Util.axios.get('/trees')).data
      await this.sustainability.fetch()
    } catch (e) {
      this.errorMessage = Util.toErrorMessage(e)
    }

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.map.center.lat = position.coords.latitude
        this.map.center.lng = position.coords.longitude
        this.zoom = 15
      })
    }
  },
  methods: {
    gotoTreeInMap(tree) {
      this.map.center = { lat: tree.latitude, lng: tree.longitude };
      this.map.zoom = 16;
    },
    getFilteredTreeList(truncate = false) {
      const filtered = Object.values(this.filters).reduce((acc, filter) => filter.isActive() ? acc.filter(filter.predicate) : acc, this.trees);
      return truncate ? filtered.slice(0, this.treesToShow) : filtered;
    },
    isListFiltered() {
      return Object.values(this.filters).some(filter => filter.isActive());
    },
    clearFilters() {
      Object.values(this.filters).forEach(filter => {
        filter.clear();
      });
    },
  }
}
