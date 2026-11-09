import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import { AuthContext } from '../context/AuthContext.jsx'
import Login from './Login.jsx'

function mostrarLogin(iniciarSesion = vi.fn()) {
  render(
    <MemoryRouter>
      <AuthContext.Provider value={{ usuario: null, iniciarSesion }}>
        <Login />
      </AuthContext.Provider>
    </MemoryRouter>,
  )
  return iniciarSesion
}

describe('Login', () => {
  it('muestra los campos de correo y contraseña', () => {
    mostrarLogin()
    expect(screen.getByLabelText(/correo/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/contraseña/i)).toBeInTheDocument()
  })

  it('no envía el formulario si los campos están vacíos', async () => {
    const iniciarSesion = mostrarLogin()
    await userEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(screen.getByText('El campo Correo es obligatorio.')).toBeInTheDocument()
    expect(screen.getByText('El campo Contraseña es obligatorio.')).toBeInTheDocument()
    expect(iniciarSesion).not.toHaveBeenCalled()
  })

  it('envía el correo y la contraseña cuando son válidos', async () => {
    const iniciarSesion = mostrarLogin(vi.fn().mockResolvedValue())
    await userEvent.type(screen.getByLabelText(/correo/i), 'ana@correo.com')
    await userEvent.type(screen.getByLabelText(/contraseña/i), 'ClaveDePrueba1')
    await userEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(iniciarSesion).toHaveBeenCalledWith('ana@correo.com', 'ClaveDePrueba1')
  })

  it('muestra el mensaje del backend cuando las credenciales son incorrectas', async () => {
    const error = { response: { status: 401, data: 'Correo o contrasena incorrectos.' } }
    mostrarLogin(vi.fn().mockRejectedValue(error))
    await userEvent.type(screen.getByLabelText(/correo/i), 'ana@correo.com')
    await userEvent.type(screen.getByLabelText(/contraseña/i), 'ClaveIncorrecta')
    await userEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('Correo o contrasena incorrectos.')
  })
})
