package com.example

fun buildUiHtml(): String = """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sneaker Shop</title>
  <script src="https://unpkg.com/react@18/umd/react.development.js"></script>
  <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; background: #0a0a0a; color: #e5e5e5; min-height: 100vh; }

    .nav { display: flex; align-items: center; justify-content: space-between; padding: 0 2rem; height: 56px; background: #111; border-bottom: 1px solid #222; position: sticky; top: 0; z-index: 10; }
    .nav-logo { font-size: 1.1rem; font-weight: 700; letter-spacing: 0.05em; }
    .nav-links { display: flex; gap: 0.4rem; align-items: center; }
    .nav-btn { background: none; border: none; color: #aaa; font-size: 0.85rem; padding: 0.4rem 0.8rem; border-radius: 6px; cursor: pointer; transition: all 0.15s; position: relative; }
    .nav-btn:hover, .nav-btn.active { background: #1e1e1e; color: #fff; }
    .nav-user { font-size: 0.8rem; color: #555; margin-left: 0.4rem; }
    .badge-admin { background: #1a3a2a; color: #4ade80; font-size: 0.65rem; padding: 0.15rem 0.5rem; border-radius: 999px; font-weight: 700; margin-left: 0.4rem; }
    .cart-badge { position: absolute; top: 2px; right: 2px; background: #22c55e; color: #000; font-size: 0.6rem; font-weight: 800; width: 16px; height: 16px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }

    .page { max-width: 1100px; margin: 0 auto; padding: 2rem 1.5rem; }
    .page-title { font-size: 1.3rem; font-weight: 600; margin-bottom: 1.5rem; }
    .section-header { display: flex; align-items: center; gap: 0.8rem; padding-bottom: 0.6rem; border-bottom: 1px solid #1e1e1e; margin-bottom: 0.8rem; }
    .section-name { font-weight: 600; font-size: 0.95rem; }
    .section-email { font-size: 0.8rem; color: #555; }
    .user-section { margin-bottom: 2rem; }

    .auth-wrap { min-height: 100vh; display: flex; align-items: center; justify-content: center; }
    .auth-card { background: #111; border: 1px solid #222; border-radius: 14px; padding: 2.5rem 2rem; width: 100%; max-width: 380px; }
    .auth-title { font-size: 1.4rem; font-weight: 700; margin-bottom: 0.3rem; }
    .auth-sub { font-size: 0.85rem; color: #666; margin-bottom: 1.8rem; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; font-size: 0.8rem; color: #888; margin-bottom: 0.4rem; }
    input { width: 100%; background: #1a1a1a; border: 1px solid #2a2a2a; border-radius: 8px; padding: 0.65rem 0.9rem; color: #e5e5e5; font-size: 0.9rem; outline: none; transition: border-color 0.15s; }
    input:focus { border-color: #22c55e; }
    .auth-toggle { margin-top: 1.2rem; text-align: center; font-size: 0.82rem; color: #555; }
    .auth-toggle span { color: #22c55e; cursor: pointer; }
    .auth-toggle span:hover { text-decoration: underline; }

    .btn { width: 100%; padding: 0.7rem; border: none; border-radius: 8px; font-size: 0.9rem; font-weight: 600; cursor: pointer; transition: opacity 0.15s; }
    .btn-primary { background: #22c55e; color: #000; margin-top: 0.5rem; }
    .btn-primary:hover { opacity: 0.85; }
    .btn-sm { width: auto; padding: 0.45rem 1rem; font-size: 0.82rem; border-radius: 6px; border: none; cursor: pointer; font-weight: 600; transition: opacity 0.15s; }
    .btn-green { background: #22c55e; color: #000; }
    .btn-green:hover { opacity: 0.85; }
    .btn-green:disabled { background: #1a3a2a; color: #4ade80; cursor: not-allowed; opacity: 1; }
    .btn-gray { background: #1e1e1e; border: 1px solid #333; color: #aaa; }
    .btn-icon { background: none; border: none; color: #555; cursor: pointer; font-size: 1rem; padding: 0.2rem 0.4rem; border-radius: 4px; transition: color 0.15s; }
    .btn-icon:hover { color: #f87171; }

    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 1rem; }
    .product-card { background: #111; border: 1px solid #1e1e1e; border-radius: 12px; overflow: hidden; transition: border-color 0.2s; }
    .product-card:hover { border-color: #333; }
    .product-img { height: 120px; background: linear-gradient(135deg, #1a1a1a, #222); display: flex; align-items: center; justify-content: center; font-size: 3.2rem; }
    .product-body { padding: 0.9rem; }
    .product-brand { font-size: 0.7rem; color: #666; text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 0.15rem; }
    .product-name { font-size: 0.88rem; font-weight: 600; margin-bottom: 0.1rem; }
    .product-colorway { font-size: 0.75rem; color: #888; margin-bottom: 0.7rem; }
    .product-footer { display: flex; align-items: flex-end; justify-content: space-between; }
    .product-price { font-size: 0.95rem; font-weight: 700; }
    .stock-tag { font-size: 0.65rem; color: #888; }

    .order-list { display: flex; flex-direction: column; gap: 0.7rem; }
    .order-card { background: #111; border: 1px solid #1e1e1e; border-radius: 10px; padding: 0.9rem 1.1rem; display: flex; justify-content: space-between; align-items: center; }
    .order-name { font-weight: 600; font-size: 0.88rem; margin-bottom: 0.2rem; }
    .order-info { font-size: 0.78rem; color: #666; }
    .order-price { font-weight: 700; color: #22c55e; font-size: 0.9rem; }
    .status-badge { font-size: 0.68rem; font-weight: 700; padding: 0.18rem 0.55rem; border-radius: 999px; background: #1a3a2a; color: #4ade80; }

    .cart-item { background: #111; border: 1px solid #1e1e1e; border-radius: 10px; padding: 0.9rem 1.1rem; display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.7rem; }
    .cart-controls { display: flex; align-items: center; gap: 0.8rem; }
    .qty-ctrl { display: flex; align-items: center; gap: 0.4rem; background: #1a1a1a; border-radius: 6px; padding: 0.2rem 0.4rem; }
    .qty-btn { background: none; border: none; color: #aaa; cursor: pointer; font-size: 1rem; padding: 0 0.3rem; }
    .qty-btn:hover { color: #fff; }
    .qty-num { min-width: 20px; text-align: center; font-size: 0.85rem; font-weight: 600; }
    .cart-total { display: flex; justify-content: space-between; align-items: center; padding: 1.2rem 0 0; border-top: 1px solid #1e1e1e; margin-top: 0.5rem; }
    .total-label { font-size: 1rem; font-weight: 600; }
    .total-value { font-size: 1.2rem; font-weight: 700; color: #22c55e; }

    .form-card { background: #111; border: 1px solid #222; border-radius: 12px; padding: 1.5rem; max-width: 480px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .form-actions { display: flex; gap: 0.7rem; margin-top: 1rem; }

    .msg { font-size: 0.83rem; padding: 0.6rem 0.9rem; border-radius: 7px; margin-bottom: 1rem; }
    .msg-error { background: #2a0a0a; color: #f87171; border: 1px solid #450a0a; }
    .msg-success { background: #0a2a14; color: #4ade80; border: 1px solid #14532d; }
    .empty { color: #555; font-size: 0.9rem; text-align: center; padding: 3rem; }
  </style>
</head>
<body>
  <div id="root"></div>
  <script type="text/babel">
    const { useState, useEffect } = React;
    const api = axios.create({ baseURL: '/' });
    const authHeader = (token) => ({ headers: { Authorization: 'Bearer ' + token } });

    /* ── Auth ─────────────────────────────────────────────────── */
    function AuthPage({ onLogin, onRegister, msg, setMsg }) {
      const [mode, setMode] = useState('login');
      const [name, setName] = useState('');
      const [email, setEmail] = useState('');
      const [password, setPassword] = useState('');

      const submit = async (e) => {
        e.preventDefault(); setMsg('');
        if (mode === 'login') await onLogin(email, password);
        else await onRegister(name, email, password);
      };

      return (
        <div className="auth-wrap">
          <div className="auth-card">
            <div className="auth-title">👟 Sneaker Shop</div>
            <div className="auth-sub">{mode === 'login' ? 'Entre na sua conta' : 'Crie sua conta'}</div>
            {msg && <div className={'msg ' + (msg.startsWith('✓') ? 'msg-success' : 'msg-error')}>{msg}</div>}
            <form onSubmit={submit}>
              {mode === 'register' && (
                <div className="form-group">
                  <label>Nome</label>
                  <input value={name} onChange={e => setName(e.target.value)} placeholder="Seu nome" required />
                </div>
              )}
              <div className="form-group">
                <label>Email</label>
                <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="email@exemplo.com" required />
              </div>
              <div className="form-group">
                <label>Senha</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="••••••" required />
              </div>
              <button type="submit" className="btn btn-primary">{mode === 'login' ? 'Entrar' : 'Criar conta'}</button>
            </form>
            <div className="auth-toggle">
              {mode === 'login'
                ? <span>Não tem conta? <span onClick={() => { setMode('register'); setMsg(''); }}>Cadastre-se</span></span>
                : <span>Já tem conta? <span onClick={() => { setMode('login'); setMsg(''); }}>Entrar</span></span>
              }
            </div>
          </div>
        </div>
      );
    }

    /* ── Navbar ───────────────────────────────────────────────── */
    function Navbar({ email, role, page, setPage, onLogout, cartCount }) {
      return (
        <nav className="nav">
          <div className="nav-logo">👟 Sneaker Shop</div>
          <div className="nav-links">
            <button className={'nav-btn ' + (page === 'products' ? 'active' : '')} onClick={() => setPage('products')}>Produtos</button>
            {role !== 'admin' && (
              <button className={'nav-btn ' + (page === 'cart' ? 'active' : '')} onClick={() => setPage('cart')}>
                🛒
                {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
              </button>
            )}
            {role !== 'admin' && (
              <button className={'nav-btn ' + (page === 'orders' ? 'active' : '')} onClick={() => setPage('orders')}>Pedidos</button>
            )}
            {role === 'admin' && (
              <button className={'nav-btn ' + (page === 'adminOrders' ? 'active' : '')} onClick={() => setPage('adminOrders')}>Todos os Pedidos</button>
            )}
            {role === 'admin' && (
              <button className={'nav-btn ' + (page === 'add' ? 'active' : '')} onClick={() => setPage('add')}>+ Produto</button>
            )}
            <span className="nav-user">
              {email}
              {role === 'admin' && <span className="badge-admin">ADMIN</span>}
            </span>
            <button className="nav-btn" onClick={onLogout}>Sair</button>
          </div>
        </nav>
      );
    }

    /* ── Products ─────────────────────────────────────────────── */
    function Products({ onAddToCart, role }) {
      const [products, setProducts] = useState([]);

      useEffect(() => {
        api.get('/products').then(r => setProducts(r.data)).catch(() => {});
      }, []);

      return (
        <div className="page">
          <div className="page-title">Catálogo</div>
          {products.length === 0
            ? <div className="empty">Nenhum produto encontrado</div>
            : (
              <div className="grid">
                {products.map(p => (
                  <div className="product-card" key={p.id}>
                    <div className="product-img">👟</div>
                    <div className="product-body">
                      <div className="product-brand">{p.brand}</div>
                      <div className="product-name">{p.name}</div>
                      <div className="product-colorway">{p.colorway}</div>
                      <div className="product-footer">
                        <div>
                          <div className="product-price">R$ {p.price.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</div>
                          <div className="stock-tag">{p.stock} em estoque</div>
                        </div>
                        {role !== 'admin' && (
                          <button
                            className="btn-sm btn-green"
                            disabled={p.stock === 0}
                            onClick={() => onAddToCart(p)}
                          >
                            {p.stock === 0 ? 'Esgotado' : '+ Carrinho'}
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )
          }
        </div>
      );
    }

    /* ── Cart ─────────────────────────────────────────────────── */
    function Cart({ cart, onUpdateQty, onRemove, onCheckout, checkoutMsg }) {
      const total = cart.reduce((sum, i) => sum + i.product.price * i.quantity, 0);

      return (
        <div className="page">
          <div className="page-title">Carrinho</div>
          {cart.length === 0
            ? <div className="empty">Carrinho vazio — adicione produtos no catálogo</div>
            : (
              <>
                {cart.map(item => (
                  <div className="cart-item" key={item.product.id}>
                    <div>
                      <div className="order-name">{item.product.name}</div>
                      <div className="order-info">{item.product.brand} · {item.product.colorway}</div>
                    </div>
                    <div className="cart-controls">
                      <div className="qty-ctrl">
                        <button className="qty-btn" onClick={() => onUpdateQty(item.product.id, item.quantity - 1)}>−</button>
                        <span className="qty-num">{item.quantity}</span>
                        <button className="qty-btn" onClick={() => onUpdateQty(item.product.id, item.quantity + 1)}>+</button>
                      </div>
                      <span className="order-price">R$ {(item.product.price * item.quantity).toLocaleString('pt-BR', {minimumFractionDigits: 2})}</span>
                      <button className="btn-icon" onClick={() => onRemove(item.product.id)}>✕</button>
                    </div>
                  </div>
                ))}
                <div className="cart-total">
                  <span className="total-label">Total</span>
                  <span className="total-value">R$ {total.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</span>
                </div>
                <div style={{marginTop: '1rem', display: 'flex', justifyContent: 'flex-end'}}>
                  <button className="btn-sm btn-green" onClick={onCheckout}>Finalizar Compra</button>
                </div>
                {checkoutMsg && <div className={'msg ' + (checkoutMsg.startsWith('✓') ? 'msg-success' : 'msg-error')} style={{marginTop: '1rem'}}>{checkoutMsg}</div>}
              </>
            )
          }
        </div>
      );
    }

    /* ── My Orders ────────────────────────────────────────────── */
    function MyOrders({ token, userId }) {
      const [orders, setOrders] = useState([]);
      const [products, setProducts] = useState({});

      useEffect(() => {
        api.get('/products').then(r => {
          const m = {}; r.data.forEach(p => { m[p.id] = p; }); setProducts(m);
        }).catch(() => {});
        api.get('/orders/' + userId, authHeader(token)).then(r => setOrders(r.data)).catch(() => {});
      }, []);

      return (
        <div className="page">
          <div className="page-title">Meus Pedidos</div>
          {orders.length === 0
            ? <div className="empty">Nenhum pedido ainda</div>
            : (
              <div className="order-list">
                {orders.map(o => {
                  const prod = products[o.productId];
                  return (
                    <div className="order-card" key={o.id}>
                      <div>
                        <div className="order-name">{prod ? prod.name : 'Produto #' + o.productId}</div>
                        <div className="order-info">{prod && prod.colorway + ' · '}Qtd: {o.quantity} · {o.createdAt ? o.createdAt.substring(0, 10) : ''}</div>
                      </div>
                      <div style={{textAlign: 'right'}}>
                        <div className="order-price">R$ {o.totalPrice.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</div>
                        <span className="status-badge">{o.status}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )
          }
        </div>
      );
    }

    /* ── Admin Orders ─────────────────────────────────────────── */
    function AdminOrders({ token }) {
      const [users, setUsers] = useState([]);
      const [orders, setOrders] = useState([]);
      const [products, setProducts] = useState({});
      const [loading, setLoading] = useState(true);

      useEffect(() => {
        Promise.all([
          api.get('/users', authHeader(token)),
          api.get('/orders', authHeader(token)),
          api.get('/products')
        ]).then(([ur, or, pr]) => {
          setUsers(ur.data);
          setOrders(or.data);
          const m = {}; pr.data.forEach(p => { m[p.id] = p; }); setProducts(m);
          setLoading(false);
        }).catch(() => setLoading(false));
      }, []);

      if (loading) return <div className="page"><div className="empty">Carregando...</div></div>;

      const byUser = {};
      orders.forEach(o => { if (!byUser[o.userId]) byUser[o.userId] = []; byUser[o.userId].push(o); });
      const usersWithOrders = users.filter(u => byUser[u.id] && byUser[u.id].length > 0);

      return (
        <div className="page">
          <div className="page-title">Todos os Pedidos</div>
          {usersWithOrders.length === 0
            ? <div className="empty">Nenhum pedido ainda</div>
            : usersWithOrders.map(u => (
              <div className="user-section" key={u.id}>
                <div className="section-header">
                  <span className="section-name">{u.name}</span>
                  <span className="section-email">{u.email}</span>
                </div>
                <div className="order-list">
                  {byUser[u.id].map(o => {
                    const prod = products[o.productId];
                    return (
                      <div className="order-card" key={o.id}>
                        <div>
                          <div className="order-name">{prod ? prod.name : 'Produto #' + o.productId}</div>
                          <div className="order-info">{prod && prod.colorway + ' · '}Qtd: {o.quantity} · {o.createdAt ? o.createdAt.substring(0, 10) : ''}</div>
                        </div>
                        <div style={{textAlign: 'right'}}>
                          <div className="order-price">R$ {o.totalPrice.toLocaleString('pt-BR', {minimumFractionDigits: 2})}</div>
                          <span className="status-badge">{o.status}</span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            ))
          }
        </div>
      );
    }

    /* ── Add Product ──────────────────────────────────────────── */
    function AddProduct({ token, setPage }) {
      const [form, setForm] = useState({ name: '', brand: '', colorway: '', description: '', price: '', stock: '' });
      const [msg, setMsg] = useState('');
      const set = (k, v) => setForm(Object.assign({}, form, {[k]: v}));

      const submit = async (e) => {
        e.preventDefault();
        try {
          await api.post('/products', {
            name: form.name, brand: form.brand, colorway: form.colorway,
            description: form.description || null, price: parseFloat(form.price), stock: parseInt(form.stock)
          }, authHeader(token));
          setMsg('✓ Produto adicionado!');
          setTimeout(() => { setMsg(''); setPage('products'); }, 1500);
        } catch (e) {
          const err = e.response && e.response.data && e.response.data.error;
          setMsg(err || 'Erro ao adicionar produto');
        }
      };

      return (
        <div className="page">
          <div className="page-title">Adicionar Produto</div>
          <div className="form-card">
            {msg && <div className={'msg ' + (msg.startsWith('✓') ? 'msg-success' : 'msg-error')}>{msg}</div>}
            <form onSubmit={submit}>
              <div className="form-group"><label>Nome</label>
                <input value={form.name} onChange={e => set('name', e.target.value)} placeholder="Air Jordan 1 Retro" required />
              </div>
              <div className="form-row">
                <div className="form-group"><label>Marca</label>
                  <input value={form.brand} onChange={e => set('brand', e.target.value)} placeholder="Jordan" required />
                </div>
                <div className="form-group"><label>Colorway</label>
                  <input value={form.colorway} onChange={e => set('colorway', e.target.value)} placeholder="Chicago" required />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group"><label>Preço (R$)</label>
                  <input type="number" step="0.01" min="0" value={form.price} onChange={e => set('price', e.target.value)} placeholder="2800.00" required />
                </div>
                <div className="form-group"><label>Estoque</label>
                  <input type="number" min="0" value={form.stock} onChange={e => set('stock', e.target.value)} placeholder="5" required />
                </div>
              </div>
              <div className="form-group"><label>Descrição (opcional)</label>
                <input value={form.description} onChange={e => set('description', e.target.value)} placeholder="Detalhes do tênis..." />
              </div>
              <div className="form-actions">
                <button type="submit" className="btn-sm btn-green">Adicionar</button>
                <button type="button" className="btn-sm btn-gray" onClick={() => setPage('products')}>Cancelar</button>
              </div>
            </form>
          </div>
        </div>
      );
    }

    /* ── App ──────────────────────────────────────────────────── */
    function App() {
      const [token, setToken]   = useState(localStorage.getItem('token') || '');
      const [userId, setUserId] = useState(parseInt(localStorage.getItem('userId')) || null);
      const [email, setEmail]   = useState(localStorage.getItem('email') || '');
      const [role, setRole]     = useState(localStorage.getItem('role') || '');
      const [page, setPage]     = useState(token ? 'products' : 'login');
      const [msg, setMsg]       = useState('');
      const [cart, setCart]     = useState([]);
      const [checkoutMsg, setCheckoutMsg] = useState('');

      const cartCount = cart.reduce((sum, i) => sum + i.quantity, 0);

      const addToCart = (product) => {
        const existing = cart.find(i => i.product.id === product.id);
        if (existing) {
          setCart(cart.map(i => i.product.id === product.id ? Object.assign({}, i, {quantity: i.quantity + 1}) : i));
        } else {
          setCart([...cart, {product, quantity: 1}]);
        }
      };

      const updateCartQty = (productId, qty) => {
        if (qty <= 0) setCart(cart.filter(i => i.product.id !== productId));
        else setCart(cart.map(i => i.product.id === productId ? Object.assign({}, i, {quantity: qty}) : i));
      };

      const removeFromCart = (productId) => setCart(cart.filter(i => i.product.id !== productId));

      const checkout = async () => {
        setCheckoutMsg('');
        let errors = 0;
        for (const item of cart) {
          try {
            await api.post('/orders', {productId: item.product.id, quantity: item.quantity}, authHeader(token));
          } catch (e) { errors++; }
        }
        if (errors === 0) {
          setCart([]);
          setCheckoutMsg('✓ Compra finalizada com sucesso!');
          setTimeout(() => { setCheckoutMsg(''); setPage('orders'); }, 2000);
        } else {
          setCheckoutMsg(errors + ' pedido(s) falharam. Verifique o estoque.');
        }
      };

      const handleLogin = async (em, pw) => {
        try {
          const r = await api.post('/users/login', {email: em, password: pw});
          localStorage.setItem('token', r.data.token); localStorage.setItem('userId', r.data.userId);
          localStorage.setItem('email', r.data.email); localStorage.setItem('role', r.data.role);
          setToken(r.data.token); setUserId(r.data.userId); setEmail(r.data.email); setRole(r.data.role);
          setPage('products'); setMsg('');
        } catch (e) { setMsg('Credenciais inválidas'); }
      };

      const handleRegister = async (name, em, pw) => {
        try {
          await api.post('/users/register', {name, email: em, password: pw});
          setMsg('✓ Conta criada! Faça login.');
        } catch (e) {
          const err = e.response && e.response.data && e.response.data.error;
          setMsg(err || 'Erro ao criar conta');
        }
      };

      const handleLogout = () => {
        localStorage.clear(); setToken(''); setUserId(null); setEmail(''); setRole('');
        setCart([]); setPage('login'); setMsg('');
      };

      if (!token) return <AuthPage onLogin={handleLogin} onRegister={handleRegister} msg={msg} setMsg={setMsg} />;

      return (
        <div>
          <Navbar email={email} role={role} page={page} setPage={setPage} onLogout={handleLogout} cartCount={cartCount} />
          {page === 'products'    && <Products onAddToCart={addToCart} role={role} />}
          {page === 'cart'        && <Cart cart={cart} onUpdateQty={updateCartQty} onRemove={removeFromCart} onCheckout={checkout} checkoutMsg={checkoutMsg} />}
          {page === 'orders'      && <MyOrders token={token} userId={userId} />}
          {page === 'adminOrders' && role === 'admin' && <AdminOrders token={token} />}
          {page === 'add'         && role === 'admin' && <AddProduct token={token} setPage={setPage} />}
        </div>
      );
    }

    ReactDOM.createRoot(document.getElementById('root')).render(<App />);
  </script>
</body>
</html>
""".trimIndent()
