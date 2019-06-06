import router from './router/index.js'
import Util from './util.js'

export default {
  name: 'app',
  data () {
	return {
	  routerName: '',
	  currentUsername: ''
	} 
  },
  watch: {
	$route(to, from) {
	  this.routerName = to.name
	  if (this.routerName !== 'Login') {
		if (!Util.getSessionGuid()) {
		  router.push('/login')
		  return
        }
		this.currentUsername = Util.getCurrentUsername()
	  }
	}  
  },
  created() {
    this.routerName = router.history.current.name
    this.currentUsername = Util.getCurrentUsername()
    
	if (this.routerName !== 'Login' && !Util.getSessionGuid()) {
	  router.push('/login')  
    }
  },
  methods: {
    async logout () {
      try {
    	const sessionGuid = Util.getSessionGuid()
    	if (sessionGuid) await Util.axios.post(`/logout?sessionGuid=${sessionGuid}`)
      } catch (e) {} finally {
    	Util.destroySession()
    	router.push('/login')
      }
    }
  }
}