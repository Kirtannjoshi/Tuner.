const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const fs = require('fs');
const path = require('path');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 3000;

// Set up logging stream
const accessLogStream = fs.createWriteStream(
  path.join(__dirname, 'tuner-access.log'),
  { flags: 'a' }
);

app.use(cors());
app.use(morgan('combined', { stream: accessLogStream }));
app.use(morgan('dev')); // Also log to console

app.get('/api/health', (req, res) => {
    res.json({ status: 'healthy', version: '1.0' });
});

// Primary Endpoint: Proxy a stream URL, handling CORS and Analytics Logging
app.get('/api/proxy', async (req, res) => {
    const streamUrl = req.query.url;
    const stationId = req.query.stationId || 'unknown';

    if (!streamUrl) {
        return res.status(400).json({ error: 'Missing stream URL parameter.' });
    }

    console.log(`[STREAM REQ] Station: ${stationId} -> ${streamUrl}`);

    try {
        const response = await axios({
            method: 'get',
            url: streamUrl,
            responseType: 'stream',
            timeout: 8000
        });

        // Forward headers loosely to avoid browser CORS blocks
        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Content-Type', response.headers['content-type'] || 'audio/mpeg');
        res.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
        
        // Pipe the live stream
        response.data.pipe(res);

        response.data.on('error', (err) => {
            console.error(`[STREAM ERR] Break on ${stationId}:`, err.message);
            res.end();
        });

    } catch (err) {
        console.error(`[PROXY ERR] Failed to proxy ${streamUrl}`, err.message);
        return res.status(502).json({ error: 'Gateway Proxy Stream Failure' });
    }
});

// Logs errors for offline analytics
app.post('/api/log/error', express.json(), (req, res) => {
    const { event, details, network } = req.body;
    console.error(`[CLIENT ERR] [${network || 'unknown'}] Event: ${event} -> ${details}`);
    res.sendStatus(202);
});

app.listen(PORT, () => {
    console.log(`Tuner Backend Server running on http://localhost:${PORT}`);
});
