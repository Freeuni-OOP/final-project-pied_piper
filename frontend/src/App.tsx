import React from 'react'
import { Routes, Route, Link, Outlet } from 'react-router-dom'

const Layout: React.FC = () => (
  <div className="min-h-screen flex flex-col">
	<header className="bg-white shadow-sm">
	  <div className="max-w-4xl mx-auto p-4">
		<Link to="/" className="text-xl font-semibold">LecturBoxd</Link>
	  </div>
	</header>
	<main className="flex-1 max-w-4xl mx-auto p-4 w-full">
	  <Outlet />
	</main>
	<footer className="bg-white border-t">
	  <div className="max-w-4xl mx-auto p-4 text-sm text-slate-500">© LecturBoxd</div>
	</footer>
  </div>
)

const Home: React.FC = () => (
  <div>
	<h1 className="text-2xl font-bold mb-2">Welcome to LecturBoxd</h1>
	<p className="text-slate-600">This is the minimal frontend baseline. Navigate to other features when available.</p>
  </div>
)

export default function App() {
  return (
	<Routes>
	  <Route path="/" element={<Layout />}>
		<Route index element={<Home />} />
	  </Route>
	</Routes>
  )
}
