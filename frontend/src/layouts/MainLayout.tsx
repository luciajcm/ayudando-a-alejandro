// src/components/layouts/MainLayout.tsx
import { Outlet } from 'react-router-dom'
import Navbar from './Navbar' // Ajusta la ruta si es necesario

function MainLayout() {
  return (
    <>
      <Navbar />
      {/* El <Outlet/> renderizará el componente de la ruta hija (Home, Nosotros, etc.) */}
      <main>
        <Outlet />
      </main>
    </>
  )
}

export default MainLayout