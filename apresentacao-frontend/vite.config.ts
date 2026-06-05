import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/reservas': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/pautas': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/votos': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/unidades': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/usuarios': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
