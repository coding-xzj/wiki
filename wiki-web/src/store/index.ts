import { createStore } from "vuex";
import SessionStorage from "../hooks/session-storage";
const USER = "USER";

const store = createStore({
  state: {
    user: SessionStorage.get(USER) || {},
  },
  mutations: {
    setUser(state, user) {
      console.log("store user：", user);
      state.user = user;
      SessionStorage.set(USER, user);
    },
  },
  actions: {},
  modules: {},
});

export default store;
