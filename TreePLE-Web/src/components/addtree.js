import Util from '../util.js'

function toErrorMessage (e) {
  return typeof e.response === 'undefined'
    ? 'Server is not responding! Is it running?'
    : e.response.data.message
}
export default {
  name: 'addtree',
  data () {
    return {
      newTree: { },
      errorMessage: '',
      successMessage: '',
      municipalities: [
    	"Ahuntsic-Cartierville", "Anjou", "Côte-des-Neiges--Notre-Dame-de-Grâce",
    	"Lachine", "LaSalle", "Le Plateau-Mont-Royal", "L'Île-Bizard--Saint-Geneviève",
    	"Mercier--Hochelaga-Maisonneuve", "Montréal-Nord", "Outremont", "Pierrefonds-Roxboro",
    	"Rivière-des-Prairies--Pointe-aux-Trembles", "Rosemont-La Petite-Patrie",
    	"Saint-Laurent", "Saint-Léonard", "Verdun", "Ville-Marie", "Villeray--Saint-Michel--Parc-Extension",
    	"Baie-d'Urfé", "Beaconsfield", "Côte-Saint-Luc", "Dollard-Des Ormeaux",
    	"Dorval", "Hampstead", "Kirkland", "Mont-Royal", "Montréal-Est",
    	"Montréal-Ouest", "Pointe-Claire", "Sainte-Anne-de-Bellevue", "Senneville", "Westmount"  
      ],
      landTypes: [
        "Residential", "Institutional", "Park", "Municipal"
      ]
    }
  },
  methods: {
    plantTree: function (newTree) {
      const sessionGuid = Util.getSessionGuid()
      Util.axios.post('/plant' + Util.toUrlArgs(Object.assign({sessionGuid: sessionGuid, overrideAccess: true}, newTree)))
      .then(response => {
        // JSON responses are automatically parsed.
        this.newTree = { }
        this.successMessage = 'Tree created successfully.'
        this.errorMessage = '';
      })
      .catch(e => {
    	this.successMessage = ''
    	this.errorMessage = Util.toErrorMessage(e)
      });
    }
  }
}
