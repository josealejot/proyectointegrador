import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { mensajeError } from '../api/http.js'
import * as api from '../api/recursos.js'
import { useAuth } from '../context/AuthContext.jsx'
import { formatoFecha, formatoHora, formatoMoneda } from '../utils/formato.js'
import { hoy } from '../utils/validaciones.js'

// Pagina de inicio: resumen del dia y de los ingresos
export default function Inicio() {
  const { usuario } = useAuth()
  const [datos, setDatos] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    async function cargar() {
      try {
        const [citas, clientes, resumen] = await Promise.all([
          api.consultarTodos('/citas'),
          api.consultarTodos('/clientes'),
          api.consultarResumenBarberos(),
        ])
        setDatos({ citas, clientes, resumen })
      } catch (fallo) {
        setError(mensajeError(fallo))
      }
    }
    cargar()
  }, [])

  if (error) {
    return (
      <section className="pagina">
        <p className="alerta alerta-error" role="alert">{error}</p>
      </section>
    )
  }
  if (!datos) {
    return (
      <section className="pagina">
        <p className="estado-tabla">Cargando resumen…</p>
      </section>
    )
  }

  const fechaHoy = hoy()
  const citasHoy = datos.citas.filter((cita) => cita.fecha === fechaHoy && cita.estado !== 'Cancelada')
  const pendientes = datos.citas.filter((cita) => cita.estado === 'Pendiente')
  const ingresos = datos.resumen.reduce((suma, barbero) => suma + Number(barbero.ingresos), 0)
  const clientesActivos = datos.clientes.filter((cliente) => cliente.activo).length

  // Proximas citas pendientes, de la mas cercana a la mas lejana
  const proximas = pendientes
    .filter((cita) => cita.fecha >= fechaHoy)
    .sort((a, b) => `${a.fecha}${a.hora}`.localeCompare(`${b.fecha}${b.hora}`))
    .slice(0, 6)

  const mayorIngreso = Math.max(1, ...datos.resumen.map((barbero) => Number(barbero.ingresos)))

  return (
    <section className="pagina">
      <header className="pagina-cabecera">
        <div>
          <h1>Hola, {usuario.nombre.split(' ')[0]}</h1>
          <p>Así va la barbería hoy, {formatoFecha(fechaHoy)}.</p>
        </div>
        <Link to="/citas" className="boton boton-primario">
          Ir a la agenda
        </Link>
      </header>

      <dl className="indicadores">
        <div className="indicador">
          <dt>Citas de hoy</dt>
          <dd>{citasHoy.length}</dd>
        </div>
        <div className="indicador">
          <dt>Citas pendientes</dt>
          <dd>{pendientes.length}</dd>
        </div>
        <div className="indicador">
          <dt>Clientes activos</dt>
          <dd>{clientesActivos}</dd>
        </div>
        <div className="indicador">
          <dt>Ingresos por citas atendidas</dt>
          <dd>{formatoMoneda(ingresos)}</dd>
        </div>
      </dl>

      <div className="dos-columnas">
        <article className="tarjeta bloque">
          <h2>Próximas citas</h2>
          {proximas.length === 0 ? (
            <p className="estado-tabla">No hay citas pendientes. Agenda una desde el módulo de citas.</p>
          ) : (
            <ul className="lista-citas">
              {proximas.map((cita) => (
                <li key={cita.codcita}>
                  <span className="cita-hora numero">{formatoHora(cita.hora)}</span>
                  <span className="cita-detalle">
                    <strong>{cita.nomcliente}</strong>
                    <span>
                      {cita.nomservicio} con {cita.nombarbero}
                    </span>
                  </span>
                  <span className="cita-fecha">{cita.fecha === fechaHoy ? 'Hoy' : formatoFecha(cita.fecha)}</span>
                </li>
              ))}
            </ul>
          )}
        </article>

        <article className="tarjeta bloque">
          <h2>Ingresos por barbero</h2>
          {datos.resumen.length === 0 ? (
            <p className="estado-tabla">Aparecerán cuando haya citas marcadas como atendidas.</p>
          ) : (
            <ul className="lista-barras">
              {datos.resumen.map((barbero) => (
                <li key={barbero.idbarbero}>
                  <div className="barra-texto">
                    <strong>{barbero.nombrebarbero}</strong>
                    <span className="numero">
                      {formatoMoneda(barbero.ingresos)} · {barbero.citas} {barbero.citas === 1 ? 'cita' : 'citas'}
                    </span>
                  </div>
                  <div className="barra">
                    <div className="barra-relleno" style={{ width: `${(Number(barbero.ingresos) / mayorIngreso) * 100}%` }} />
                  </div>
                </li>
              ))}
            </ul>
          )}
        </article>
      </div>
    </section>
  )
}
