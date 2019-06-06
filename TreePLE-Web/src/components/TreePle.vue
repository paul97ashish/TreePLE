<template>
  <div id="treeple">
    <div v-if="errorMessage" class="message-container error">
      <span>⚠ {{ errorMessage }}</span>
    </div>

    <!-- Tree List -->
    <div class="tree-table-container white-carte">
      <table class="table table-hover tree-table">
        <thead class="thead">
          <th>Status</th>
          <th>Health</th>
          <th>Mark</th>
          <th>Species</th>
          <th>Canopy Diameter (m)</th>
          <th>Height (m)</th>
          <th>Land Type</th>
          <th>Latitude</th>
          <th>Longitude</th>
        </thead>
        <tbody>
          <tr v-if="!getFilteredTreeList().length">
            <td colspan="10" class="text-muted">No tree data found. <a v-if="isListFiltered()" href="javascript:void(0)" @click="clearFilters()">Clear filters.</a></td>
          </tr>
          <tr v-for="tree in getFilteredTreeList(!showAllTrees)"
            :key="tree.id"
            @click="gotoTreeInMap(tree)"
            style="cursor: pointer">
            <td>{{ tree.state.status }}</td>
            <td>{{ tree.state.health }}</td>
            <td>{{ tree.state.mark }}</td>
            <td>{{ tree.species }}</td>
            <td>{{ Math.round(tree.canopyDiameterMeters * 100) / 100 || "" }}</td>
            <td>{{ Math.round(tree.heightMeters * 100) / 100 || "" }}</td>
            <td>{{ tree.landType }}</td>
            <td>{{ Math.round(tree.latitude * 10000) / 10000 || "" }}</td>
            <td>{{ Math.round(tree.longitude * 10000) / 10000 || "" }}</td>
          </tr>
          <tr v-if="!showAllTrees && getFilteredTreeList().length > treesToShow"
            @click="showAllTrees = true"
            style="cursor: pointer">
            <td id="expander" colspan="10" class="text-muted">Show all trees ({{getFilteredTreeList().length - treesToShow}} more)</td>
          </tr>
          <tr v-else-if="getFilteredTreeList().length > treesToShow"
            @click="showAllTrees = false"
            style="cursor: pointer">
            <td colspan="10" class="text-muted">Show fewer results</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Map -->
    <div class="map-container">
      <gmap-map
        class="google-map"
        :center="map.center"
        :zoom="map.zoom">

        <gmap-marker
          v-for="tree in trees"
          :key="tree.id"
          :position="{lat:tree.latitude, lng:tree.longitude}"
          :clickable="true"
          :draggable="true"
          @click="center={lat:tree.latitude, lng:tree.longitude}">
        </gmap-marker>

        <gmap-circle
          :center="map.circle.center"
          :radius="parseFloat(map.circle.radius*1000)"
          :draggable="false"
          :editable="false">
        </gmap-circle>

      </gmap-map>

      <!-- Map Filters -->
      <div class="map-action-container">
        <div class="map-action">
          <div class="action-header">
            <b>Proximity Filter</b>
            <a @click="filters.circle.reset()" class="action-reset" href="javascript:void(0)">reset</a>
          </div>
          <div class="action-content">
            <ul>
              <li v-for="(value, label) in map.radiusOptions" :key="value">
                <input type="radio" :id="value" v-model="map.circle.radius" :value="value">
                <label :for="value">{{label}}</label>
              </li>
              <li>
                <label for="custom-radius">Custom (km)</label>
                <input min="0" type="number" name="custom-radius" v-model="map.circle.radius">
              </li>
            </ul>
          </div>
        </div>
        <div class="map-action">
          <div class="action-header">
            <b>Height Filter (m)</b>
            <a @click="filters.height.reset()" class="action-reset" href="javascript:void(0)">reset</a>
          </div>
          <div class="action-content">
            <ul>
              <li>
                <input id="height-filter-enabled" type="checkbox" v-model="filters.height.enabled">
                <label for="height-filter-enabled">Enabled</label>
              </li>
              <li>
                <label for="min-height">Min:</label>
                <input min="0" type="number" name="min-height" v-model="filters.height.min">
              </li>

              <li>
                <label for="max-height">Max:</label>
                <input min="0" type="number" name="max-height" v-model="filters.height.max">
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Sustainability attributes -->
    <div class="sustainability-container">
      <div class="sustainability-header">
        <h1 class="sustainability-header-text">Sustainability: Measurements &amp; Forecasting</h1>
        <a class="sustainability-refresh" href="javascript:void(0)" @click="sustainability.fetch()">refresh</a>
      </div>

      <span v-if="sustainability.calculatingValues" class="text-muted">
        Calculating...
      </span>
      <div v-else>
        <p>
          Based on the properties of trees planted, we can determine various measurements of sustainability.
          One such measurement is the offset of carbon emissions trees provide.
          Using this quality we can derive a method to forecast how many trees could be required to offset future carbon emissions.
        </p>
        <ul>
          <li><b>Carbon offset of all trees:</b> {{ sustainability.carbonOffset.all.toFixed(3) }} kg</li>
          <li><b>Carbon offset of trees selected:</b> {{ sustainability.carbonOffset.selected.toFixed(3) }} kg</li>
        </ul>
        <br>
        <p><b>Forecast:</b></p>
        <input type="number" placeholder="Enter carbon offset" v-model="sustainability.carbonTarget"><br>
        <span v-if="sustainability.carbonTarget">Based on the average carbon offset per tree, to offset <b>{{sustainability.carbonTarget}} kg</b> of emissions, <b>{{sustainability.treesToPlant()}} trees</b> must be planted in the surrounding ecosystem.</span>
      </div>
    </div>

    <footer style="color: white">Copyright © {{ new Date().getFullYear() }} TreePLE Project 6. All rights reserved.</footer>
  </div>
</template>
<script src="./treeple.js"></script>
<style lang="scss" scoped>
  #treeple {
    display: flex;
    align-items: center;
    flex-direction: column;

    margin-top: 30px;

    .tree-table-container {
      display: inline-block;
      margin: 0 auto;
      width: 1000px;
      padding: 0;
      border-radius: 0;
      box-shadow: 3px 5px 8px 0px rgba(0,0,0,0.3);
    }
    
    .tree-table {
      margin-bottom: 0;
    }
    
    .map-container {
      display: flex;
      justify-content: center;
      margin: 10px auto;
      width: 1000px;
      height: 600px;

      .google-map {
        flex-grow: 1;
        background: rgba(29, 131, 34, 0.267);
      }

      .map-action-container {
        display: flex;
        flex-direction: column;

        margin-left: 15px;
        .map-action {
          min-width: 215px;
          display: inline-block;
          background-color: white;

          margin-top: 10px;
          &:first-child {
            margin-top: 0;
          }

          ul {
            text-align: left;
            list-style: none;
            padding: 0;
            margin: 0;
          }

          input[type=number] {
            width: 80px;
          }
          
          padding: 15px;

          .action-header {
            border-bottom: solid #d2d2d2 1px;
            margin-bottom: 5px;
          }

          .action-reset {
            float: right;
            font-size: 14px;
          }
        }
      }
    }

    .sustainability-container {
      background-color: white;
      width: 1000px;
      padding: 20px;
      margin-bottom: 40px;
      text-align: left;

      ul {
        text-align: center;
        list-style: none;
        padding: 0;
        margin: 0;
      }

      .sustainability-header {
        .sustainability-header-text {
          display: inline-block;
        }
        .sustainability-refresh {
          float: right;
        }
      }
    }
  }
</style>
