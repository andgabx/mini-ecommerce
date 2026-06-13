package com.example

fun buildDashboardHtml(): String = """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sneaker E-commerce — Monitor</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
      background: #0f0f0f;
      color: #e0e0e0;
      min-height: 100vh;
      padding: 2rem;
    }
    header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 2rem;
    }
    h1 { font-size: 1.4rem; font-weight: 600; letter-spacing: 0.02em; }
    h1 span { color: #888; font-weight: 400; font-size: 1rem; }
    #updated { font-size: 0.8rem; color: #555; }
    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
      gap: 1rem;
    }
    .card {
      background: #1a1a1a;
      border: 1px solid #2a2a2a;
      border-radius: 10px;
      padding: 1.2rem 1.4rem;
      transition: border-color 0.3s;
    }
    .card.up   { border-left: 3px solid #22c55e; }
    .card.down { border-left: 3px solid #ef4444; }
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 0.6rem;
    }
    .name { font-weight: 600; font-size: 0.95rem; }
    .badge {
      font-size: 0.7rem;
      font-weight: 700;
      padding: 0.2rem 0.6rem;
      border-radius: 999px;
      letter-spacing: 0.05em;
    }
    .badge.up   { background: #14532d; color: #4ade80; }
    .badge.down { background: #450a0a; color: #f87171; }
    .url { font-size: 0.75rem; color: #555; margin-bottom: 0.4rem; }
    .failures { font-size: 0.75rem; color: #666; }
    .failures.warning { color: #f59e0b; }
    footer { margin-top: 2rem; font-size: 0.75rem; color: #333; text-align: center; }
  </style>
</head>
<body>
  <header>
    <h1>Sneaker E-commerce <span>/ Service Monitor</span></h1>
    <div id="updated">Aguardando...</div>
  </header>
  <div class="grid" id="grid"></div>
  <footer>Atualiza a cada 5 segundos · Gateway :8080</footer>

  <script>
    async function refresh() {
      try {
        const res = await fetch('/status');
        const services = await res.json();
        const grid = document.getElementById('grid');
        grid.innerHTML = services.map(s => {
          const cls = s.status === 'UP' ? 'up' : 'down';
          const warn = s.failures > 0 ? ' warning' : '';
          return '<div class="card ' + cls + '">' +
            '<div class="card-header">' +
              '<span class="name">' + s.name + '</span>' +
              '<span class="badge ' + cls + '">' + s.status + '</span>' +
            '</div>' +
            '<div class="url">' + s.url + '</div>' +
            '<div class="failures' + warn + '">falhas consecutivas: ' + s.failures + '</div>' +
          '</div>';
        }).join('');
        document.getElementById('updated').textContent =
          'Atualizado: ' + new Date().toLocaleTimeString('pt-BR');
      } catch(e) {
        document.getElementById('updated').textContent = 'Erro ao buscar status';
      }
    }
    refresh();
    setInterval(refresh, 5000);
  </script>
</body>
</html>
""".trimIndent()
