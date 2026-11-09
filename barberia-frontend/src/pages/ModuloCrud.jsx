import { Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { mensajeError } from '../api/http.js'
import * as api from '../api/recursos.js'
import FormularioModal from '../components/FormularioModal.jsx'
import { filtrarFilas, formatoFecha, formatoHora, formatoMoneda } from '../utils/formato.js'

// Muestra el valor de una celda segun el formato de la columna
function Celda({ columna, fila }) {
  const valor = fila[columna.campo]
  switch (columna.formato) {
    case 'moneda':
      return <span className="numero">{formatoMoneda(valor)}</span>
    case 'fecha':
      return <span className="sin-salto">{formatoFecha(valor)}</span>
    case 'hora':
      return <span className="numero">{formatoHora(valor)}</span>
    case 'minutos':
      return `${valor} min`
    case 'activo':
      return <span className={`etiqueta ${valor ? 'etiqueta-ok' : 'etiqueta-apagada'}`}>{valor ? 'Activo' : 'Inactivo'}</span>
    case 'estado':
      return <span className={`etiqueta etiqueta-${String(valor).toLowerCase()}`}>{valor}</span>
    default:
      return valor
  }
}

// Pagina generica de un modulo: tabla, buscador, filtros y formulario.
// Sirve para citas, clientes, barberos, servicios y usuarios.
export default function ModuloCrud({ modulo }) {
  const [filas, setFilas] = useState([])
  const [opciones, setOpciones] = useState({})
  const [catalogos, setCatalogos] = useState({})
  const [cargando, setCargando] = useState(true)
  const [errorCarga, setErrorCarga] = useState('')
  const [busqueda, setBusqueda] = useState('')
  const [filtros, setFiltros] = useState({})
  const [formulario, setFormulario] = useState(null) // null = cerrado, { fila } = abierto
  const [porEliminar, setPorEliminar] = useState(null)
  const [aviso, setAviso] = useState(null)

  // Consulta general del modulo (GET al endPoint del controlador)
  const cargar = useCallback(async () => {
    try {
      setErrorCarga('')
      setFilas(await api.consultarTodos(modulo.endpoint))
    } catch (error) {
      setErrorCarga(mensajeError(error))
    } finally {
      setCargando(false)
    }
  }, [modulo])

  // Carga las opciones de las listas desplegables y botones de radio
  const cargarOpciones = useCallback(async () => {
    try {
      const listas = await api.consultarCatalogos()
      const resultado = {}
      for (const campo of modulo.campos) {
        if (campo.catalogo) {
          resultado[campo.nombre] = (listas[campo.catalogo] || []).map((opcion) => ({ valor: opcion, texto: opcion }))
        }
        if (campo.recurso) {
          const registros = await api.consultarTodos(campo.recurso.endpoint)
          resultado[campo.nombre] = registros.map((registro) => ({
            valor: String(registro[campo.recurso.valor]),
            texto: campo.recurso.texto(registro),
            activo: registro.activo,
          }))
        }
      }
      setCatalogos(listas)
      setOpciones(resultado)
    } catch (error) {
      setErrorCarga(mensajeError(error))
    }
  }, [modulo])

  useEffect(() => {
    cargar()
    cargarOpciones()
  }, [cargar, cargarOpciones])

  // El aviso de exito desaparece solo
  useEffect(() => {
    if (!aviso) return undefined
    const temporizador = setTimeout(() => setAviso(null), 4000)
    return () => clearTimeout(temporizador)
  }, [aviso])

  const visibles = useMemo(
    () => filtrarFilas(filas, modulo, busqueda, filtros),
    [filas, modulo, busqueda, filtros],
  )

  function opcionesDeFiltro(filtro) {
    if (filtro.opciones) return filtro.opciones
    if (filtro.catalogo) return (catalogos[filtro.catalogo] || []).map((opcion) => ({ valor: opcion, texto: opcion }))
    if (filtro.recurso) return opciones[filtro.recurso] || []
    return []
  }

  async function guardar(datos) {
    if (formulario.fila) {
      await api.modificar(modulo.endpoint, formulario.fila[modulo.id], datos)
      setAviso({ tipo: 'ok', texto: 'Registro modificado con éxito.' })
    } else {
      await api.adicionar(modulo.endpoint, datos)
      setAviso({ tipo: 'ok', texto: 'Registro adicionado con éxito.' })
    }
    setFormulario(null)
    await cargar()
  }

  async function confirmarEliminar() {
    try {
      await api.eliminar(modulo.endpoint, porEliminar[modulo.id])
      setAviso({ tipo: 'ok', texto: 'Registro eliminado con éxito.' })
      await cargar()
    } catch (error) {
      // Por ejemplo: el cliente tiene citas y el backend no deja eliminarlo
      setAviso({ tipo: 'error', texto: mensajeError(error) })
    } finally {
      setPorEliminar(null)
    }
  }

  const hayFiltros = busqueda || Object.values(filtros).some(Boolean)
  const etiquetaNuevo = modulo.clave === 'citas' ? 'Agendar cita' : `Nuevo ${modulo.singular}`

  return (
    <section className="pagina">
      <header className="pagina-cabecera">
        <div>
          <h1>{modulo.titulo}</h1>
          <p>{modulo.descripcion}</p>
        </div>
        <button type="button" className="boton boton-primario" onClick={() => setFormulario({ fila: null })}>
          <Plus size={18} /> {etiquetaNuevo}
        </button>
      </header>

      <div className="barra-filtros">
        <label className="buscador">
          <Search size={18} aria-hidden="true" />
          <span className="solo-lectores">Buscar en {modulo.titulo.toLowerCase()}</span>
          <input
            type="search"
            placeholder={`Buscar ${modulo.singular}…`}
            value={busqueda}
            maxLength={60}
            onChange={(evento) => setBusqueda(evento.target.value)}
          />
        </label>

        {(modulo.filtros || []).map((filtro) => (
          <label key={filtro.campo} className="filtro">
            <span>{filtro.etiqueta}</span>
            {filtro.tipo === 'fecha' ? (
              <input
                type="date"
                value={filtros[filtro.campo] || ''}
                onChange={(evento) => setFiltros({ ...filtros, [filtro.campo]: evento.target.value })}
              />
            ) : (
              <select
                value={filtros[filtro.campo] || ''}
                onChange={(evento) => setFiltros({ ...filtros, [filtro.campo]: evento.target.value })}
              >
                <option value="">Todos</option>
                {opcionesDeFiltro(filtro).map((opcion) => (
                  <option key={opcion.valor} value={opcion.valor}>
                    {opcion.texto}
                  </option>
                ))}
              </select>
            )}
          </label>
        ))}

        {hayFiltros && (
          <button
            type="button"
            className="boton boton-texto"
            onClick={() => {
              setBusqueda('')
              setFiltros({})
            }}
          >
            Limpiar
          </button>
        )}
      </div>

      {errorCarga && (
        <p className="alerta alerta-error" role="alert">
          {errorCarga}{' '}
          <button type="button" className="boton boton-texto" onClick={cargar}>
            Reintentar
          </button>
        </p>
      )}

      <div className="tarjeta tabla-contenedor">
        <table>
          <thead>
            <tr>
              {modulo.columnas.map((columna) => (
                <th key={columna.campo} scope="col">
                  {columna.titulo}
                </th>
              ))}
              <th scope="col" className="columna-acciones">
                Acciones
              </th>
            </tr>
          </thead>
          <tbody>
            {visibles.map((fila) => (
              <tr key={fila[modulo.id]}>
                {modulo.columnas.map((columna) => (
                  <td key={columna.campo} data-titulo={columna.titulo}>
                    <Celda columna={columna} fila={fila} />
                  </td>
                ))}
                <td className="columna-acciones">
                  <button
                    type="button"
                    className="boton-icono"
                    onClick={() => setFormulario({ fila })}
                    aria-label={`Editar ${modulo.singular} ${fila[modulo.id]}`}
                    title="Editar"
                  >
                    <Pencil size={17} />
                  </button>
                  <button
                    type="button"
                    className="boton-icono boton-icono-peligro"
                    onClick={() => setPorEliminar(fila)}
                    aria-label={`Eliminar ${modulo.singular} ${fila[modulo.id]}`}
                    title="Eliminar"
                  >
                    <Trash2 size={17} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {cargando && <p className="estado-tabla">Cargando {modulo.titulo.toLowerCase()}…</p>}
        {!cargando && !errorCarga && visibles.length === 0 && (
          <p className="estado-tabla">
            {filas.length === 0
              ? `Todavía no hay registros. Usa "${etiquetaNuevo}" para crear el primero.`
              : 'Ningún registro coincide con la búsqueda o los filtros.'}
          </p>
        )}
      </div>

      {!cargando && filas.length > 0 && (
        <p className="conteo">
          {visibles.length} de {filas.length} registros
        </p>
      )}

      {formulario && (
        <FormularioModal
          modulo={modulo}
          fila={formulario.fila}
          opciones={opciones}
          onGuardar={guardar}
          onCerrar={() => setFormulario(null)}
        />
      )}

      {porEliminar && (
        <div className="fondo-modal">
          <div className="modal modal-chico" role="alertdialog" aria-modal="true" aria-labelledby="titulo-eliminar">
            <h2 id="titulo-eliminar">¿Eliminar este {modulo.singular}?</h2>
            <p>Esta acción no se puede deshacer.</p>
            <footer className="modal-pie">
              <button type="button" className="boton boton-secundario" onClick={() => setPorEliminar(null)}>
                Cancelar
              </button>
              <button type="button" className="boton boton-peligro" onClick={confirmarEliminar}>
                Eliminar
              </button>
            </footer>
          </div>
        </div>
      )}

      {aviso && (
        <p className={`aviso aviso-${aviso.tipo}`} role="status">
          {aviso.texto}
        </p>
      )}
    </section>
  )
}
