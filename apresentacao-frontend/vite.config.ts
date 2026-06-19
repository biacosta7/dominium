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
      '/assembleias': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/financeiro': {
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
      '/funcionarios': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/ordens-servico': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
