import { defineStore } from 'pinia'

// application config store
export const useAppConfig = defineStore('appConfig', {
  state: () => ({
    locale: "zhCN",
    theme: "auto",
    expanded: [] as string[],
    menu: '[]',
  }),
  getters: {
    getLocale: (state) => state.locale,
    getTheme: (state) => state.theme,
  },
  actions: {
    setLocale(locale: string) {
      this.locale = locale
    },
    setTheme(theme: string) {
      this.theme = theme
    },
  },
  persist: true,
})
