<template>
  <div id="app">
    <div id="background-image"></div>

    <div v-if="this.routerName !== 'Login'">
    <img class="logo" src="./assets/logo2.png" >
    <br>
    <span class="welcome-msg">Welcome, {{ this.currentUsername }}!</span>
    <div class="navigation">
      <router-link :to="{name: 'TreePLE'}">
  	    <button
          class="btn btn-default"
          :class="{ selected: this.routerName == 'TreePLE' }">
          Dashboard
        </button>
  	  </router-link>
      <router-link :to="{name: 'addtree'}">
        <button
          class="btn btn-default"
          :class="{ selected: this.routerName == 'addtree' }">
          Add Tree
        </button>
      </router-link>
      <button id="button-logout" class="btn btn-default" @click="logout()">Logout</button>
	</div>
	</div>
	
    <router-view></router-view>
  </div>
</template>

<script src="./app.js">
</script>

<style>
.white-carte {
  display: inline-block;
  padding: 25px;
  border-radius: 5px;
  background-color: white;
  box-shadow: 3px 5px 8px 0px rgba(0,0,0,0.3);
}
.btn-default:hover {
  background-color: rgb(207, 207, 207);
}
.btn.selected {
  background-color: rgb(139, 255, 110);
}
body {
  background-color: #003500cb;
}
#background-image {
  display: block;
  position: fixed;
  top:0;
  left: 0;
  z-index: -1;
  width: 100%;
  min-height: 100%;
  height: auto;

  background: url('./assets/trees-bg.jpg') no-repeat center center fixed;
  background-size: cover;
  filter: blur(10px);
}
.logo {
  width: 120px;
  margin: 15px 15px;
  border-radius: 5px;
}
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: Black;
}
.welcome-msg {
  color: rgb(0, 252, 34);
  font-size: 22px;
  text-shadow: 0px 0px 4px rgb(0, 0, 0);
}
.navigation {
  margin: 15px;
}
button {
  margin: 5px;
}
.message-container {
  background-color: black;
  color: white;
  border-radius: 5px;
  padding: 7px;
  margin: 10px;
}
.message-container.success {
  background-color: #388e3c;
}
.message-container.error {
  background-color: #d32f2f;
}
</style>
