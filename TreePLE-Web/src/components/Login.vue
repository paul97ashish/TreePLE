<template>
<div id="login" class="white-carte">
  <img class="logo" src="../assets/logo2.png" >
  <h2>TreePLE</h2>
  <div v-if="errorMessage" class="message-container error">
    <span>{{ errorMessage }}</span>
  </div>
  <input id="login-username" class="form-control" type="text" v-model="username" placeholder="Username">
  <input id="login-password"  class="form-control" type="password" v-model="password" placeholder="Password">
  <div>
    <button id="button-login" class="btn btn-default" @click="login(username)">Login</button>  
    <button id="button-signup" class="btn btn-primary" @click="signup(username)">Signup</button>
  </div>
  <br>
  <span v-if="loggingIn">Logging in...</span>
</div>
</template>
<script src="./login.js"></script>
<style>
  #login {
    position: absolute;
    top: 50%;
    left: 50%;    
    transform: translate(-50%, -80%);

    display: flex;
    flex-direction: column;

    align-items: center;
    justify-content: center;
    width: 400px;
    margin: 0 auto;

    /* background-color: rgba(255, 255, 255, 0.144); */
    /* box-shadow: 0px 0px 10px 0px rgba(0,0,0,0.3); */
    /* padding: 25px;  */
    /* border-radius: 5px; */
  }
  #login * {
    max-width: 50%;
    margin-top: 10px;
  }
</style>