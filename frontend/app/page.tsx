"use client"

import { useEffect, useRef } from "react"

export default function Page() {
  const mountRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    // Dynamically import and mount the Vite app
    const loadViteApp = async () => {
      if (mountRef.current) {
        const { default: App } = await import("../src/App")
        const React = await import("react")
        const ReactDOM = await import("react-dom/client")

        const root = ReactDOM.createRoot(mountRef.current)
        root.render(React.createElement(App))
      }
    }

    loadViteApp()
  }, [])

  return <div ref={mountRef} className="w-full h-full" />
}
