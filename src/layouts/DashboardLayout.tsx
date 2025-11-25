import type React from "react"
import { Outlet } from "react-router-dom"
import DashboardNavbar from "./DashboardNavBar"
import ProfileGuard from "../auth/ProfileGuard" // <--- Importar

const DashboardLayout: React.FC = () => {
  return (
    <ProfileGuard> {/* <--- Envolver todo aquí */}
      <div className="min-h-screen bg-black">
        <DashboardNavbar />

        <main className="animate-in fade-in duration-300">
          <Outlet />
        </main>
      </div>
    </ProfileGuard>
  )
}

export default DashboardLayout