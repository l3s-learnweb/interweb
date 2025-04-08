import { defineConfig } from 'vite'
import preact from '@preact/preset-vite'

// https://vite.dev/config/
export default defineConfig({
    // depending on your application, base can also be "/"
    base: '',
    server: {
        // this ensures that the browser opens upon server start
        open: true,
        // this sets a default port to 3000, you can change this
        port: 3000,
    },
  plugins: [preact()],
})
