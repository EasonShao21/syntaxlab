import { defineConfig } from 'vite'; import vue from '@vitejs/plugin-vue';
export default defineConfig({
  base: process.env.GITHUB_ACTIONS === 'true' ? '/syntaxlab/' : '/',
  plugins:[vue()],
  server:{proxy:{'/api':'http://localhost:8080'}}
});
