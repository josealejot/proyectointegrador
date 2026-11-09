import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import RutaProtegida from './components/RutaProtegida.jsx'
import { modulos } from './configs/modulos.js'
import Inicio from './pages/Inicio.jsx'
import Login from './pages/Login.jsx'
import ModuloCrud from './pages/ModuloCrud.jsx'
import Registro from './pages/Registro.jsx'

export default function App() {
  return (
    <Routes>
      {/* Rutas publicas */}
      <Route path="/login" element={<Login />} />
      <Route path="/registro" element={<Registro />} />

      {/* Rutas restringidas: solo con sesion iniciada */}
      <Route element={<RutaProtegida />}>
        <Route element={<Layout />}>
          <Route path="/" element={<Inicio />} />
          {modulos
            .filter((modulo) => !modulo.soloAdmin)
            .map((modulo) => (
              <Route
                key={modulo.clave}
                path={modulo.ruta}
                element={<ModuloCrud key={modulo.clave} modulo={modulo} />}
              />
            ))}

          {/* Rutas restringidas por rol: solo el administrador */}
          <Route element={<RutaProtegida rol="Administrador" />}>
            {modulos
              .filter((modulo) => modulo.soloAdmin)
              .map((modulo) => (
                <Route
                  key={modulo.clave}
                  path={modulo.ruta}
                  element={<ModuloCrud key={modulo.clave} modulo={modulo} />}
                />
              ))}
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
