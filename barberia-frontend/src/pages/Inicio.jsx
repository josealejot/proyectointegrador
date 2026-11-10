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
        const [citas, clientes, servicios, barberos, resumen] = await Promise.all([
          api.consultarTodos('/citas'),
          api.consultarTodos('/clientes'),
          api.consultarTodos('/servicios'),
          api.consultarTodos('/barberos'),
          api.consultarResumenBarberos(),
        ])
        setDatos({ citas, clientes, servicios, barberos, resumen })
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
  const citasBase = usuario.rol === 'Barbero' && usuario.identificacion
    ? datos.citas.filter((c) => String(c.idbarbero) === String(usuario.identificacion))
    : usuario.rol === 'Cliente' && usuario.identificacion
      ? datos.citas.filter((c) => String(c.idcliente) === String(usuario.identificacion))
      : datos.citas

  const citasHoy = citasBase.filter((cita) => cita.fecha === fechaHoy && cita.estado !== 'Cancelada' && cita.estado !== 'Incumplida')
  const pendientes = citasBase.filter((cita) => cita.estado === 'Pendiente')
  const ingresos = datos.resumen.reduce((suma, barbero) => suma + Number(barbero.ingresos), 0)
  const clientesActivos = datos.clientes.filter((cliente) => cliente.activo).length

  // Proximas citas pendientes, de la mas cercana a la mas lejana
  const proximas = pendientes
    .filter((cita) => cita.fecha >= fechaHoy)
    .sort((a, b) => `${a.fecha}${a.hora}`.localeCompare(`${b.fecha}${b.hora}`))
    .slice(0, 6)

  const mayorIngreso = Math.max(1, ...datos.resumen.map((barbero) => Number(barbero.ingresos)))
  const esCliente = usuario.rol === 'Cliente'
  const esBarbero = usuario.rol === 'Barbero'
  const serviciosActivos = (datos.servicios || []).filter((s) => s.activo)
  const barberosActivos = (datos.barberos || []).filter((b) => b.activo)
  const atendidasBarbero = citasBase.filter((c) => c.estado === 'Atendida').length

  return (
    <section className="pagina">
      <header className="pagina-cabecera">
        <div>
          <h1>Hola, {usuario.nombre.split(' ')[0]}</h1>
          <p>
            {esCliente
              ? `Bienvenido a La Navaja. Hoy es ${formatoFecha(fechaHoy)} · Horario de atención: 08:00 a 20:00.`
              : esBarbero
                ? `Tu agenda de trabajo para hoy, ${formatoFecha(fechaHoy)}.`
                : `Así va la barbería hoy, ${formatoFecha(fechaHoy)}.`}
          </p>
        </div>
        <Link to="/citas" className="boton boton-primario">
          {esCliente ? 'Agendar o ver mis citas' : esBarbero ? 'Ir a mi agenda' : 'Ir a la agenda'}
        </Link>
      </header>

      {esCliente ? (
        <dl className="indicadores">
          <div className="indicador">
            <dt>Mis citas de hoy</dt>
            <dd>{citasHoy.length}</dd>
          </div>
          <div className="indicador">
            <dt>Mis citas pendientes</dt>
            <dd>{pendientes.length}</dd>
          </div>
          <div className="indicador">
            <dt>Barberos disponibles</dt>
            <dd>{barberosActivos.length}</dd>
          </div>
          <div className="indicador">
            <dt>Servicios disponibles</dt>
            <dd>{serviciosActivos.length}</dd>
          </div>
        </dl>
      ) : esBarbero ? (
        <dl className="indicadores">
          <div className="indicador">
            <dt>Mis citas de hoy</dt>
            <dd>{citasHoy.length}</dd>
          </div>
          <div className="indicador">
            <dt>Mis citas pendientes</dt>
            <dd>{pendientes.length}</dd>
          </div>
          <div className="indicador">
            <dt>Mis citas cumplidas</dt>
            <dd>{atendidasBarbero}</dd>
          </div>
          <div className="indicador">
            <dt>Servicios del catálogo</dt>
            <dd>{serviciosActivos.length}</dd>
          </div>
        </dl>
      ) : (
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
      )}

      <div className="dos-columnas">
        <article className="tarjeta bloque">
          <h2>{esCliente ? 'Mis próximas citas' : esBarbero ? 'Mis próximas citas por atender' : 'Próximas citas'}</h2>
          {proximas.length === 0 ? (
            <p className="estado-tabla">
              {esCliente
                ? 'No tienes citas pendientes. Entra a "Citas" para agendar la tuya.'
                : 'No hay citas pendientes en este momento.'}
            </p>
          ) : (
            <ul className="lista-citas">
              {proximas.map((cita) => (
                <li key={cita.codcita}>
                  <span className="cita-hora numero">{formatoHora(cita.hora)}</span>
                  <span className="cita-detalle">
                    <strong>{esCliente ? cita.nomservicio : cita.nomcliente}</strong>
                    <span>
                      {esCliente
                        ? `Barbero: ${cita.nombarbero} · ${formatoMoneda(cita.precio)}`
                        : `${cita.nomservicio} con ${cita.nombarbero}`}
                    </span>
                  </span>
                  <span className="cita-fecha">{cita.fecha === fechaHoy ? 'Hoy' : formatoFecha(cita.fecha)}</span>
                </li>
              ))}
            </ul>
          )}
        </article>

        {esCliente || esBarbero ? (
          <article className="tarjeta bloque">
            <h2>Catálogo de servicios y precios</h2>
            <ul className="lista-citas">
              {serviciosActivos.map((servicio) => (
                <li key={servicio.codservicio}>
                  <span className="cita-hora numero">{servicio.duracion}m</span>
                  <span className="cita-detalle">
                    <strong>{servicio.nomservicio}</strong>
                    <span>Duración estimada: {servicio.duracion} minutos</span>
                  </span>
                  <span className="numero">
                    <strong>{formatoMoneda(servicio.precio)}</strong>
                  </span>
                </li>
              ))}
            </ul>
          </article>
        ) : (
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
        )}
      </div>
    </section>
  )
}
