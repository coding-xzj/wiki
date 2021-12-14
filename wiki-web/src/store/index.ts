import { createStore } from "vuex";
import SessionStorage from "@/util/session-storage";
const USER = "USER";

const store = createStore({
  state: {
    user: SessionStorage.get(USER) || {},
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
      SessionStorage.set(USER, user);
    },
  },
  actions: {},
  modules: {},
});

export default store;
