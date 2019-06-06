<template>
<div>

<!--    <th>Latitude</th>
    <th>Longitude</th>
    <th>Height</th>
    <th>Canopy</th>
    <th>Species</th>
    <th>Municipality</th>
    <th>Land Type</th>-->
  <div class="white-carte inputs">
    <div v-if="successMessage" class="message-container success">
      <span>{{ successMessage }}</span>
    </div>
    <div v-if="errorMessage" class="message-container error">
      <span>{{ errorMessage }}</span>
    </div>

    <input id="input-latitude" class="form-control" type="number" v-model="newTree.latitude" placeholder="Latitude">
    <input id="input-longitude" class="form-control" type="number" v-model="newTree.longitude" placeholder="Longitude">
    <input id="input-height" class="form-control" type="number" v-model="newTree.height" placeholder="Height">
    <input id="input-canopy" class="form-control" type="number" v-model="newTree.canopy" placeholder="Canopy Diameter">
    <input id="input-species" class="form-control" type="text" v-model="newTree.species" placeholder="Species">
    <select id="select-municipality" v-model="newTree.mun">
      <option disabled selected>Select a municipality...</option>
      <option v-for="option in municipalities" v-bind:value="option.replace(/[^A-Za-z]/g, '')">
        {{ option }}
      </option>
    </select>
    <select id="select-landType" v-model="newTree.landType">
      <option disabled selected>Select a land type...</option>
      <option v-for="option in landTypes" v-bind:value="option.replace(/[^A-Za-z]/g, '')">
        {{ option }}
      </option>
    </select>
    <br>
    <button id="button-plant" class="btn btn-success" @click="plantTree(newTree)">Plant!</button>
  </div>
  
  </div>
</template>
<script src="./addtree.js"></script>
<style scoped>
.inputs * {
  margin-top: 10px
}
.google-map {
  width: 800px;
  height: 600px;
  margin: 0 auto;
  background: gray;
}
</style>
