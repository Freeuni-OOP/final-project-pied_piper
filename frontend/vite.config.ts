import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Vite bundler and dev-server configuration for the LecturBoxd frontend.
export default defineConfig({
  plugins: [react()],
  define: {
    global: 'window',
  },
  server: {
    port: 5173,
    strictPort: false,
    host: true,
  },
})
