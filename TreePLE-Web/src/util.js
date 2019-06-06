import axios from 'axios'
import router from './router/index.js'

// Use appropriate config
const config = process.env.NODE_ENV === 'production'
  ? require('../config').build
  : require('../config').dev

const frontendUrl = 'http://' + config.host + ':' + config.port
const backendUrl = 'http://' + config.backendHost + ':' + config.backendPort

const AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
  storeSession (sessionGuid, username) {
    localStorage.setItem('activeUsername', username)
    localStorage.setItem('sessionGuid', sessionGuid)
  },
  getSessionGuid () {
    return localStorage.getItem('sessionGuid')
  },
  getCurrentUsername () {
    return localStorage.getItem('activeUsername')
  },
  destroySession () {
    localStorage.removeItem('sessionGuid')
    localStorage.removeItem('activeUsername')
  },
  toErrorMessage (e) {
    const msg = typeof e.response === 'undefined'
      ? 'Server is not responding! Is it running?'
      : e.response.data.message

    if (msg.indexOf('Session has expired') !== -1) {
      this.destroySession()
      router.push('/login')
    }
    return msg
  },
  toUrlArgs (obj) {
    let args = ''
    for (let propertyName in obj) {
      if (obj.hasOwnProperty(propertyName)) {
        args += `&${propertyName}=${encodeURIComponent(obj[propertyName])}`
      }
    }
    return '?' + args
  },
  axios: AXIOS
}
