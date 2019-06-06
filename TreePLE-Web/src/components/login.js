import router from '../router/index.js'
import Util from '../util.js'

function toErrorMessage(e) {
  return typeof e.response === 'undefined'
    ? 'Server is not responding! Is it running?'
    : e.response.data.message;
}

export default {
  name: 'Login',
  data () {
    return {
      username: '',
      password: '',
      loggingIn: false,
      errorMessage: ''
    }
  },
  created() {
    if (Util.getSessionGuid()) {
      router.push('/')
	}
  },
  methods: {
    async login() {
      if (!this.username && this.username !== "0") {
    	this.errorMessage = "Please provide a username."
    	return
      }
      this.loggingIn = true;
      this.errorMessage = '';
      try {
        const sessionGuid = (await Util.axios.post('/login' + Util.toUrlArgs({
          username: this.username,
          password: this.password
        }))).data
        Util.storeSession(sessionGuid, this.username)
        router.push('/')
      } catch (e) {
        this.errorMessage = toErrorMessage(e)
      } finally {
    	  this.loggingIn = false;
      }
    },
    async signup() {
      if (!this.username && this.username !== "0") {
        this.errorMessage = "Please provide a username."
      	return
      }
      this.loggingIn = true;
      this.errorMessage = '';
      try {
        const sessionGuid = (await Util.axios.post('/signup' + Util.toUrlArgs({
          username: this.username,
          password: this.password
        }))).data
        Util.storeSession(sessionGuid, this.username)
        router.push('/')
      } catch (e) {
        this.errorMessage = toErrorMessage(e)
      } finally {
        this.loggingIn = false;
      }
    }
  }
}
