const express = require('express');
const nunjucks = require('nunjucks');
const path = require('path');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

// Proxy /uploads requests to Spring Boot backend
app.use('/uploads', createProxyMiddleware({
    target: 'http://localhost:8080',
    changeOrigin: true,
}));

// Static files (CSS, JS, images)
app.use(express.static(path.join(__dirname, 'public')));

// Nunjucks config
nunjucks.configure('views', {
    autoescape: true,
    express: app
});

// Example route
app.get('/', async (req, res) => {
    const response = await fetch('http://localhost:8080/api/events');
    const events = await response.json();

    res.render('index.njk', { events });
});

app.get('/events', (req, res) => {
    res.render('events.njk');
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Frontend running on http://localhost:${PORT}`);
});
