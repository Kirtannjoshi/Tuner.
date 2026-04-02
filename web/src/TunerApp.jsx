import { useState, useEffect, useRef, useCallback } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Hls from "hls.js";
import './index.css';

// ── Components ──────────────────────────────────────────────────────────────
function AnimatedMesh() {
  return (
    <div className="mesh-container">
      <motion.div className="mesh-blob" animate={{ x: [0, 400, 0], y: [0, 200, 0], scale: [1, 1.5, 1] }} transition={{ duration: 25, repeat: Infinity, ease: "easeInOut" }} style={{ width: 800, height: 800, left: '-20%', top: '-20%', background: 'var(--accent-primary)', opacity: 0.3 }} />
      <motion.div className="mesh-blob" animate={{ x: [0, -300, 0], y: [0, 400, 0], scale: [1, 1.2, 1] }} transition={{ duration: 20, repeat: Infinity, ease: "easeInOut" }} style={{ width: 600, height: 600, right: '-10%', top: '20%', background: 'var(--accent-tertiary)', opacity: 0.2 }} />
      <motion.div className="mesh-blob" animate={{ x: [0, 200, 0], y: [0, -300, 0], scale: [1, 1.4, 1] }} transition={{ duration: 30, repeat: Infinity, ease: "easeInOut" }} style={{ width: 700, height: 700, left: '30%', bottom: '-20%', background: '#9146FF', opacity: 0.2 }} />
    </div>
  );
}

function HeroBanner() {
  const [isDownloading, setIsDownloading] = useState(false);
  const [showToast, setShowToast] = useState(false);

  const handleDownload = () => {
    setIsDownloading(true);
    setTimeout(() => {
      window.location.href = "/download/tuner.apk";
      setTimeout(() => {
        setIsDownloading(false);
        setShowToast(true);
        setTimeout(() => setShowToast(false), 5000);
      }, 3000);
    }, 150);
  };

  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
      style={{
        padding: '60px 40px', borderRadius: 32, 
        background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-dim)',
        marginBottom: 48, position: 'relative', overflow: 'hidden'
      }}
    >
      <AnimatePresence>
        {showToast && (
          <motion.div
            initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }}
            style={{
              position: 'absolute', top: 20, left: '50%', x: '-50%', zIndex: 10,
              background: 'var(--accent-primary)', color: '#fff', padding: '12px 24px',
              borderRadius: 40, fontWeight: 700, fontSize: 13, whiteSpace: 'nowrap',
              boxShadow: '0 10px 30px rgba(236, 72, 153, 0.4)'
            }}
          >
            🚀 Update Download Started!
          </motion.div>
        )}
      </AnimatePresence>
      <div style={{ position: 'relative', zIndex: 1, maxWidth: 640 }}>
        <h1 className="font-display hero-gradient" style={{ fontSize: 'min(9vw, 64px)', fontWeight: 900, lineHeight: 1, margin: '0 0 24px', letterSpacing: -2 }}>
           The World in <br/> Your Pocket.
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: 18, lineHeight: 1.5, marginBottom: 32, maxWidth: 500 }}>
           Experience high-fidelity global radio streaming, redesigned with a sober and minimalist aesthetic. Redefining how you discover sound.
        </p>
        <motion.button
          onClick={handleDownload}
          whileHover={{ scale: 1.05, boxShadow: '0 12px 40px rgba(236, 72, 153, 0.4)' }}
          whileTap={{ scale: 0.95 }}
          disabled={isDownloading}
          style={{
            display: 'inline-flex', alignItems: 'center', gap: 12, padding: '18px 32px',
            background: isDownloading ? 'var(--bg-surface)' : 'var(--accent-primary)', 
            borderRadius: 16, color: '#fff',
            border: 'none', cursor: isDownloading ? 'default' : 'pointer',
            fontWeight: 800, fontSize: 17,
            boxShadow: isDownloading ? 'none' : '0 8px 30px rgba(236, 72, 153, 0.2)',
            transition: 'all 0.3s'
          }}
        >
          {isDownloading ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
               <div className="spinner" style={{ width: 20, height: 20, borderTopColor: '#fff' }} />
               <span>Starting Download...</span>
            </div>
          ) : (
            <>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
              Download for Android
            </>
          )}
        </motion.button>
      </div>
      {/* Visual background element */}
      <div style={{ position: 'absolute', right: -60, bottom: -60, fontSize: 240, rotate: '-15deg', opacity: 0.05, filter: 'blur(2px)' }}>📻</div>
    </motion.div>
  );
}

function EqBars({ color = "var(--accent-secondary)", size = 20 }) {
  return (
    <div style={{ display: "flex", alignItems: "flex-end", gap: 3, height: size, width: size + 10 }}>
      {Array.from({ length: 4 }).map((_, i) => (
        <div key={i} className="eq-bar" style={{ background: color }} />
      ))}
    </div>
  );
}

function SkeletonCard() {
  return (
    <div className="skeleton" style={{ padding: 16, borderRadius: 16, display: "flex", flexDirection: "column", gap: 12, height: 160 }}>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <div className="skeleton" style={{ width: 64, height: 64, borderRadius: "50%" }} />
        <div className="skeleton" style={{ width: 32, height: 32, borderRadius: "50%" }} />
      </div>
      <div className="skeleton" style={{ height: 16, width: '70%', borderRadius: 4, marginTop: 'auto' }} />
      <div className="skeleton" style={{ height: 12, width: '40%', borderRadius: 4 }} />
    </div>
  );
}

function StationLogo({ src, name, size = 64, isPlaying = false }) {
  const [err, setErr] = useState(false);
  const initials = (name || "Radio").split(" ").slice(0, 2).map(w => w[0]).join("").toUpperCase();
  const hue = (name || "").split("").reduce((a, c) => a + c.charCodeAt(0), 0) % 360;

  return (
    <motion.div animate={{
      boxShadow: isPlaying ? "0 4px 14px rgba(145, 70, 255, 0.4)" : "0 4px 10px rgba(0,0,0,0.3)",
      borderColor: isPlaying ? "var(--accent-primary)" : "var(--border-light)"
    }} style={{
      width: size, height: size, borderRadius: size > 40 ? 16 : 10, flexShrink: 0, overflow: "hidden",
      background: `linear-gradient(135deg, hsl(${hue}, 60%, 40%), hsl(${(hue+100)%360}, 60%, 20%))`,
      display: "flex", alignItems: "center", justifyContent: "center",
      border: `2px solid var(--border-light)`,
    }}>
      {!err && src ? (
        <img src={src} alt={name} onError={() => setErr(true)} loading="lazy"
          style={{ width: "100%", height: "100%", objectFit: "cover" }} />
      ) : (
        <span className="font-display" style={{ fontWeight: 800, fontSize: size * 0.35, color: "#fff", letterSpacing: 1 }}>
          {initials}
        </span>
      )}
    </motion.div>
  );
}

// ── SVGs ──
function HeartIcon({ filled }) {
  return (
    <svg className="heart-icon-svg" width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"
      fill={filled ? "var(--accent-tertiary)" : "transparent"} stroke={filled ? "var(--accent-tertiary)" : "currentColor"}
      strokeLinecap="round" strokeLinejoin="round" strokeWidth="2">
      <path d="M11.996 21.054c-7.3-4.661-10.22-9.69-9.96-13.684.18-2.68 2.3-4.8 4.96-4.8 1.94 0 3.65 1.15 4.54 2.89.26.54 1.14.54 1.4 0 .9-1.74 2.6-2.89 4.54-2.89 2.66 0 4.78 2.12 4.96 4.8.26 3.99-2.66 9.02-9.96 13.684a.99.99 0 01-1.04 0z" />
    </svg>
  );
}

function MicIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 2a3 3 0 0 0-3 3v7a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3Z" />
      <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
      <line x1="12" x2="12" y1="19" y2="22" />
    </svg>
  );
}

function SearchIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="11" cy="11" r="8" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" />
    </svg>
  );
}

function PlayIcon() {
  return <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z" /></svg>;
}

function PauseIcon() {
  return <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor"><path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z" /></svg>;
}

function VolumeHighIcon() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><path d="M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07"></path></svg>;
}

function VolumeLowIcon() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path></svg>;
}

function VolumeMutedIcon() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><line x1="23" y1="1" x2="1" y2="23"></line></svg>;
}

function DiscoverIcon() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>;
}
function NearbyIcon() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>;
}
function LibraryIcon() {
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path></svg>;
}

const GENRES = [
  { label: "Top Global", tag: "top" },
  { label: "Pop", tag: "pop" },
  { label: "Rock", tag: "rock" },
  { label: "Jazz", tag: "jazz" },
  { label: "Classical", tag: "classical" },
  { label: "Hip-Hop", tag: "hiphop" },
  { label: "Electronic", tag: "electronic" },
  { label: "News", tag: "news" },
  { label: "Dance", tag: "dance" },
];

const NETWORKS = [
  { label: "BBC Radio", term: "BBC", color: "linear-gradient(135deg, #FF0000, #990000)", tag: "bbc", shadow: "rgba(255,0,0,0.4)" },
  { label: "Capital FM", term: "Capital FM", color: "linear-gradient(135deg, #0055FF, #002299)", tag: "capital", shadow: "rgba(0,85,255,0.4)" },
  { label: "Heart UK", term: "Heart", color: "linear-gradient(135deg, #FF0055, #990033)", tag: "heart", shadow: "rgba(255,0,85,0.4)" },
  { label: "NPR Networks", term: "NPR", color: "linear-gradient(135deg, #111111, #333333)", tag: "npr", shadow: "rgba(0,0,0,0.6)" },
  { label: "KISS", term: "KISS", color: "linear-gradient(135deg, #AABB00, #669900)", tag: "kiss", shadow: "rgba(170,187,0,0.4)" },
];

function countryFlag(code) {
  if (!code || code.length !== 2) return "🌐";
  return String.fromCodePoint(...[...code.toUpperCase()].map(c => 0x1F1E6 + c.charCodeAt(0) - 65));
}

const containerVariant = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.04 } }
};
const itemVariant = {
  hidden: { opacity: 0, y: 15, scale: 0.95 },
  show: { opacity: 1, y: 0, scale: 1, transition: { type: "spring", stiffness: 300, damping: 24 } },
  exit: { opacity: 0, scale: 0.9, transition: { duration: 0.15 } }
};

// ═══════════════════════════════════════════════════════════════════════════════
//  MAIN APP
// ═══════════════════════════════════════════════════════════════════════════════
export default function TunerApp() {
  const [stations, setStations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [genre, setGenre] = useState("top");
  const [query, setQuery] = useState("");
  const [debouncedQuery, setDebouncedQuery] = useState("");
  const [current, setCurrent] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [volume, setVolume] = useState(0.8);
  const [muted, setMuted] = useState(false);
  const [loadingStation, setLoadingStation] = useState(false);
  const [errorStatus, setErrorStatus] = useState("");
  const [tab, setTab] = useState("discover"); 
  const [userCountry, setUserCountry] = useState(null);
  const [listening, setListening] = useState(false);
  const [showAbout, setShowAbout] = useState(false);
  const [favorites, setFavorites] = useState(() => {
    try { return JSON.parse(localStorage.getItem("tuner_favs") || "[]"); } catch { return []; }
  });
  const [sessionDuration, setSessionDuration] = useState(0);
  const [ambientImg, setAmbientImg] = useState(null);
  const [isPlayerOpen, setPlayerOpen] = useState(false);
  const [isPerformanceMode, setPerformanceMode] = useState(false);

  const audioRef = useRef(null);
  const hlsRef = useRef(null);
  
  const isTypingSearch = query !== debouncedQuery;

  // Audio setup
  useEffect(() => {
    const audio = new Audio();
    audio.volume = volume;
    audioRef.current = audio;
    
    // REMOVED crossOrigin="anonymous" and AudioContext DSP mapping.
    // Browsers strictly block cross-origin requests required by Web Audio nodes from 3rd-party broadcast servers.
    // Retaining native playback ensures 100% station compatibility across the directory while preventing "Silent output" CORS blocks.

    const playHandler = () => { setIsPlaying(true); setLoadingStation(false); setErrorStatus(""); };
    const waitHandler = () => setLoadingStation(true);
    const errHandler = () => { 
        setLoadingStation(false); setIsPlaying(false); 
        setErrorStatus("Stream offline or unsupported");
    };

    audio.addEventListener("playing", playHandler);
    audio.addEventListener("waiting", waitHandler);
    audio.addEventListener("error", errHandler);
    audio.addEventListener("stalled", waitHandler);

    return () => {
      audio.pause();
      if (hlsRef.current) hlsRef.current.destroy();
      audio.removeEventListener("playing", playHandler);
      audio.removeEventListener("waiting", waitHandler);
      audio.removeEventListener("error", errHandler);
    };
  }, []);

  // Session Duration Tracker
  useEffect(() => {
    let interval = null;
    if (isPlaying) {
      interval = setInterval(() => {
        setSessionDuration(prev => prev + 1);
      }, 1000);
    } else {
      clearInterval(interval);
    }
    return () => clearInterval(interval);
  }, [isPlaying]);

  // Ambient Img Transition
  useEffect(() => {
    if (current?.favicon) {
      setAmbientImg(current.favicon);
    }
  }, [current]);

  useEffect(() => {
    if (audioRef.current) { audioRef.current.volume = muted ? 0 : volume; }
  }, [volume, muted]);

  useEffect(() => {
    fetch("https://get.geojs.io/v1/ip/geo.json")
      .then(r => r.json())
      .then(d => { if (d.country_code) setUserCountry(d.country_code); })
      .catch(() => {});
  }, []);

  useEffect(() => {
    const handler = setTimeout(() => { setDebouncedQuery(query); }, 600);
    return () => clearTimeout(handler);
  }, [query]);

  const fetchStations = useCallback(async (g, isNearby = false, globalSearchText = null) => {
    setLoading(true);
    const BASE = "https://de1.api.radio-browser.info/json/stations";
    let url = "";

    if (globalSearchText && globalSearchText.length >= 3) {
      url = `${BASE}/byname/${encodeURIComponent(globalSearchText)}?hidebroken=true&limit=80&order=clickcount&reverse=true`;
      setTab("search");
    } else if (isNearby && userCountry) {
      url = `${BASE}/bycountrycodeexact/${encodeURIComponent(userCountry)}?hidebroken=true&limit=60&order=clickcount&reverse=true`;
    } else {
      if (g === "top") url = `${BASE}/topvote/80`;
      else url = `${BASE}/bytagexact/${encodeURIComponent(g)}?hidebroken=true&limit=80&order=clickcount&reverse=true`;
    }

    try {
      const res = await fetch(url, { headers: { "User-Agent": "TunerApp/1.0" } });
      const data = await res.json();
      setStations(data.filter(s => s.url_resolved && s.name));
    } catch {}
    setLoading(false);
  }, [userCountry]);

  useEffect(() => {
    if (debouncedQuery.length >= 3) {
       fetchStations(null, false, debouncedQuery);
    }
    else if (tab === "nearby" && userCountry) fetchStations(null, true, null);
    else if (tab === "discover") fetchStations(genre, false, null);
  }, [tab, genre, userCountry, debouncedQuery, fetchStations]);

  useEffect(() => {
    if (query === "" && tab === "search") {
      setTab("discover");
    }
  }, [query, tab]);

  const playStation = useCallback((station) => {
    const audio = audioRef.current;
    if (!audio) return;
    setLoadingStation(true);
    setErrorStatus("");

    if (current?.stationuuid === station.stationuuid && isPlaying) {
      audio.pause(); setIsPlaying(false); setLoadingStation(false);
      return;
    }
    
    if (hlsRef.current) {
        hlsRef.current.destroy();
        hlsRef.current = null;
    }

    audio.pause();
    const url = station.url_resolved || station.url;

    if (url.includes(".m3u8") && Hls.isSupported()) {
        const hls = new Hls({ debug: false });
        hls.loadSource(url);
        hls.attachMedia(audio);
        hls.on(Hls.Events.MANIFEST_PARSED, () => {
            const playPromise = audio.play();
            if (playPromise !== undefined) playPromise.catch(() => {
                setLoadingStation(false); setErrorStatus("Offline Stream");
            });
        });
        hls.on(Hls.Events.ERROR, () => {
            setLoadingStation(false); setErrorStatus("HLS Protocol Error");
        });
        hlsRef.current = hls;
    } else {
        audio.src = url;
        audio.load();
        const playPromise = audio.play();
        if (playPromise !== undefined) playPromise.catch(() => {
            setLoadingStation(false); setErrorStatus("Offline Stream");
        });
    }

    setCurrent(station);
  }, [current, isPlaying]);

  const toggleFav = useCallback((station, e) => {
    e?.stopPropagation();
    setFavorites(prev => {
      const exists = prev.find(s => s.stationuuid === station.stationuuid);
      const next = exists ? prev.filter(s => s.stationuuid !== station.stationuuid) : [station, ...prev];
      localStorage.setItem("tuner_favs", JSON.stringify(next));
      return next;
    });
  }, []);

  const startVoiceSearch = useCallback(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) { alert("Voice search is not supported in your browser."); return; }
    const recognition = new SpeechRecognition();
    recognition.lang = 'en-US';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = () => setListening(true);
    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      setQuery(transcript);
    };
    recognition.onerror = () => setListening(false);
    recognition.onend = () => setListening(false);
    recognition.start();
  }, []);

  const isFav = (s) => favorites.some(f => f.stationuuid === s.stationuuid);

  let displayList = tab === "favorites" ? favorites : stations;
  if (query && tab === "favorites") {
    const q = query.toLowerCase();
    displayList = displayList.filter(s => s.name.toLowerCase().includes(q) || (s.tags || "").toLowerCase().includes(q));
  }

  return (
    <div style={{ display: 'flex', height: '100vh', width: '100vw', background: 'var(--bg-base)', overflow: 'hidden' }}>
      
      {/* ── INNOVATIVE BACKGROUNDS ── */}
      {!isPerformanceMode && <AnimatedMesh />}
      
      <AnimatePresence mode="wait">
        {ambientImg && !isPerformanceMode && (
          <motion.div 
            key={ambientImg}
            initial={{ opacity: 0 }} 
            animate={{ opacity: 1 }} 
            exit={{ opacity: 0 }}
            transition={{ duration: 1.5 }}
            className="ambient-bg"
          >
            <img src={ambientImg} alt="" />
            <div className="ambient-overlay" style={{ background: isPlayerOpen ? 'radial-gradient(circle at center, transparent 0%, var(--bg-base) 100%)' : 'radial-gradient(circle at center, transparent 0%, var(--bg-base) 85%)' }} />
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── NOW PLAYING OVERLAY (MOBILE-LIKE 70% ALIGNMENT) ── */}
      <AnimatePresence>
        {isPlayerOpen && current && (
          <motion.div 
            initial={{ y: '100%' }} animate={{ y: 0 }} exit={{ y: '100%' }}
            transition={{ type: "spring", stiffness: 300, damping: 30 }}
            style={{
              position: 'fixed', inset: 0, zIndex: 1000, background: 'var(--bg-base)',
              display: 'flex', flexDirection: 'column', padding: '16px 32px'
            }}
          >
            {/* Ambient Shadow Overlay for immersion */}
            <div className="ambient-overlay" style={{ opacity: 0.6 }} />

            <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', maxWidth: 460, margin: '0 auto', width: '100%' }}>
              {/* Header Chevron */}
              <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 20 }}>
                 <button onClick={() => setPlayerOpen(false)} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)', padding: 12 }}>
                    <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>
                 </button>
              </div>

              <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
                <motion.div 
                   className={isPlaying ? "breathing" : ""}
                   style={{ 
                     width: '100%', aspectRatio: '1/1', background: 'var(--bg-elevated)', borderRadius: 32, 
                     overflow: 'hidden', boxShadow: '0 24px 60px rgba(0,0,0,0.5)', marginBottom: 40,
                     border: '1px solid var(--border-dim)'
                   }}
                >
                   <img src={current.favicon} alt={current.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                </motion.div>

                <div style={{ width: '100%', textAlign: 'center', marginBottom: 40 }}>
                   <h1 className="font-display" style={{ fontSize: 32, fontWeight: 800, color: 'var(--text-primary)', marginBottom: 8, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {current.name}
                   </h1>
                   <p style={{ color: 'var(--accent-primary)', fontWeight: 800, fontSize: 13, letterSpacing: 1 }}>
                     {isPlaying ? "● LIVE STREAMING" : "PAUSED"}
                   </p>
                </div>

                {/* Integrated Session Tracker */}
                <div style={{ width: '100%', marginBottom: 48 }}>
                   <div style={{ height: 4, background: 'rgba(255,255,255,0.1)', borderRadius: 2, marginBottom: 12, position: 'relative', overflow: 'hidden' }}>
                      <div style={{ position: 'absolute', inset: 0, background: 'var(--accent-primary)', width: `${(sessionDuration % 60) * (100/60)}%`, borderRadius: 2, boxShadow: '0 0 10px var(--accent-primary)' }} />
                   </div>
                   <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-secondary)', fontSize: 12, fontWeight: 600 }}>
                      <span>{Math.floor(sessionDuration/60)}:{(sessionDuration%60).toString().padStart(2,'0')}</span>
                      <span>{countryFlag(current.countrycode)} {current.country || "GLOBAL"}</span>
                   </div>
                </div>

                {/* Minimalist Big Play Button */}
                <div style={{ display: 'flex', justifyContent: 'center', gap: 32, alignItems: 'center' }}>
                  <button onClick={(e) => toggleFav(current, e)} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: isFav(current) ? 'var(--accent-tertiary)' : 'var(--text-secondary)' }}>
                     <HeartIcon filled={isFav(current)} />
                  </button>
                  <motion.button 
                    whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}
                    onClick={() => {
                      const audio = audioRef.current;
                      if (!audio) return;
                      if (isPlaying) { audio.pause(); setIsPlaying(false); }
                      else { audio.play(); }
                    }}
                    style={{
                      width: 80, height: 80, borderRadius: '50%', background: 'var(--text-primary)', color: 'var(--bg-base)',
                      border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
                      boxShadow: '0 12px 30px rgba(255,255,255,0.2)'
                    }}
                  >
                    {isPlaying ? <PauseIcon /> : <PlayIcon />}
                  </motion.button>
                  <button style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}>
                     <NearbyIcon />
                  </button>
                </div>
              </div>
              <div style={{ height: 60 }} />
            </div>
          </motion.div>
        )}
      </AnimatePresence>
      
      {/* ── ABOUT MODAL ── */}
      <AnimatePresence>
        {showAbout && (
          <motion.div 
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            style={{
              position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(8px)',
              zIndex: 9999, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24
            }}
            onClick={() => setShowAbout(false)}
          >
            <motion.div 
              initial={{ scale: 0.9, y: 30 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.9, y: 30 }}
              transition={{ type: "spring", stiffness: 400, damping: 25 }}
              style={{
                background: 'var(--bg-elevated)', border: '1px solid var(--border-light)',
                borderRadius: 24, padding: 40, maxWidth: 500, boxShadow: '0 20px 40px rgba(0,0,0,0.5)'
              }}
              onClick={e => e.stopPropagation()}
            >
              <h2 className="font-display" style={{ fontSize: 24, marginBottom: 12 }}>Tuner Architecture</h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: 14, lineHeight: 1.6, marginBottom: 16 }}>
                A high-fidelity streaming engine aggregating over 40,000 global stations for a borderless listening experience. Bridging global cultures through sound.
              </p>
              <div style={{ background: 'rgba(236,72,153,0.05)', border: '1px solid rgba(236,72,153,0.1)', padding: 16, borderRadius: 12, marginBottom: 24 }}>
                <p style={{ color: 'var(--text-primary)', margin: 0, fontSize: 13, lineHeight: 1.5 }}>
                  <strong>Disclaimer</strong><br/>
                  Tuner leverages the Radio-Browser API to provide a free gateway to public broadcasts. We do not host or own any content.
                </p>
              </div>
              <motion.button 
                whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}
                onClick={() => setShowAbout(false)}
                style={{
                  width: '100%', padding: '14px 0', borderRadius: 12, border: 'none', background: 'var(--accent-primary)',
                  color: '#fff', fontSize: 16, fontWeight: 600, cursor: 'pointer'
                }}
              >
                Acknowledge & Close
              </motion.button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── SIDEBAR ────────────────────────────────────────────────────────── */}
      <aside style={{
        width: 'var(--sidebar-width)', background: 'var(--bg-base)', borderRight: '1px solid var(--border-dim)',
        display: 'flex', flexDirection: 'column', padding: '24px 16px', zIndex: 10, flexShrink: 0,
        height: '100vh', overflowY: 'auto'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 40, padding: '0 8px' }}>
          <img src="/logo.svg" alt="Tuner Logo" style={{ height: 46, width: 'auto', objectFit: 'contain' }} />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 32 }}>
          <p style={{ fontSize: 11, fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: 1.5, marginLeft: 8, marginBottom: 8 }}>Menu</p>
          {[
            { id: "discover", icon: <DiscoverIcon/>, label: "Discover" },
            { id: "nearby", icon: <NearbyIcon/>, label: "Nearby Region" },
            { id: "favorites", icon: <LibraryIcon/>, label: "Your Library" },
          ].map(t => (
            <button key={t.id} onClick={() => { setTab(t.id); setQuery(""); }}
              className={`sidebar-link ${tab === t.id && !query ? "active" : ""}`}
              style={{
                display: 'flex', alignItems: 'center', gap: 14, padding: '12px 14px', borderRadius: 8,
                background: tab === t.id && !query ? 'var(--bg-surface)' : 'transparent',
                color: tab === t.id && !query ? 'var(--text-primary)' : 'var(--text-secondary)',
                fontWeight: tab === t.id && !query ? 600 : 500, fontSize: 15, transition: 'all 0.2s',
                textAlign: 'left', border: 'none', cursor: 'pointer'
              }}
              onMouseEnter={e => { if (tab !== t.id || query) e.currentTarget.style.color = 'var(--text-primary)'; }}
              onMouseLeave={e => { if (tab !== t.id || query) e.currentTarget.style.color = 'var(--text-secondary)'; }}
            >
              <span style={{ display: 'flex', alignItems: 'center' }}>{t.icon}</span>
              {t.label}
            </button>
          ))}
        </div>

        {((tab === "discover" && !query) || tab === "search") && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            <p style={{ fontSize: 11, fontWeight: 700, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: 1.5, marginLeft: 8, marginBottom: 8, marginTop: 12 }}>Genres</p>
            {GENRES.map(g => (
              <button key={g.tag} onClick={() => { setGenre(g.tag); setTab("discover"); setQuery(""); }}
                className={`sidebar-link ${genre === g.tag && tab === "discover" && !query ? "active" : ""}`}
                style={{
                  display: 'flex', alignItems: 'center', gap: 14, padding: '10px 14px', borderRadius: 8,
                  background: genre === g.tag && tab === "discover" && !query ? 'rgba(145, 70, 255, 0.1)' : 'transparent',
                  color: genre === g.tag && tab === "discover" && !query ? 'var(--accent-primary)' : 'var(--text-secondary)',
                  fontWeight: 500, fontSize: 14, transition: 'all 0.2s', textAlign: 'left', border: 'none', cursor: 'pointer'
                }}
                onMouseEnter={e => { if (genre !== g.tag || tab !== "discover" || query) e.currentTarget.style.color = 'var(--text-primary)'; }}
                onMouseLeave={e => { if (genre !== g.tag || tab !== "discover" || query) e.currentTarget.style.color = 'var(--text-secondary)'; }}
              >
                {g.label}
              </button>
            ))}
          </div>
        )}

        {/* Performance & Branding */}
        <div style={{ marginTop: 'auto', paddingTop: 24, paddingLeft: 8, paddingRight: 8, borderTop: '1px solid var(--border-dim)' }}>
           {/* Performance Mode Toggle */}
           <div className="performance-toggle" onClick={() => setPerformanceMode(!isPerformanceMode)} style={{ marginBottom: 16 }}>
              <div style={{ flex: 1, fontSize: 13, fontWeight: 700, color: isPerformanceMode ? 'var(--accent-primary)' : 'var(--text-primary)' }}>Performance Mode</div>
              <div style={{ width: 10, height: 10, borderRadius: '50%', background: isPerformanceMode ? 'var(--accent-primary)' : 'rgba(255,255,255,0.1)' }} />
           </div>

          <p style={{ fontSize: 11, color: 'var(--text-secondary)', marginBottom: 12, fontWeight: 600 }}>© 2026 Tuner</p>
          <div style={{ display: 'flex', gap: 12, fontSize: 10, color: 'var(--text-secondary)', flexWrap: 'wrap' }}>
            <span className="footer-link" onClick={() => setShowAbout(true)} style={{ color: 'var(--accent-primary)', fontWeight: 600 }}>About Tuner</span>
            <span className="footer-link">Legal</span>
            <span className="footer-link">Privacy</span>
          </div>
        </div>
      </aside>

      {/* ── MAIN CONTENT ────────────────────────────────────────────────────── */}
      <main style={{
        flex: 1, background: 'linear-gradient(180deg, var(--bg-elevated) 0%, var(--bg-base) 100%)',
        paddingBottom: 'calc(var(--player-height) + 40px)', position: 'relative', overflowY: 'auto', overflowX: 'hidden'
      }}>
        
        <header className="header-glass" style={{
          position: 'sticky', top: 0, zIndex: 5, padding: '16px 40px',
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          flexWrap: 'wrap', gap: 16
        }}>
          <div>
            <AnimatePresence mode="wait">
              <motion.h2 
                key={tab === "favorites" ? "fav" : tab === "nearby" ? "near" : tab === "search" || query ? "search" : "disc"}
                initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 10 }}
                className="font-display text-gradient" style={{ fontSize: 28, fontWeight: 800, margin: 0, lineHeight: 1.1 }}
              >
                {tab === "favorites" ? "Your Library" : 
                 tab === "nearby" ? `Live in ${userCountry || "Your Area"}` : 
                 tab === "search" || query ? `Search Results` :
                 "Discover"}
              </motion.h2>
            </AnimatePresence>
            <p style={{ fontSize: 13, color: 'var(--text-secondary)', marginTop: 4 }}>
              {isTypingSearch ? "Searching network..." : `${displayList.length} stations`}
            </p>
          </div>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: 16, flexWrap: 'wrap' }}>
            <div style={{
              display: 'flex', alignItems: 'center', background: 'rgba(255, 255, 255, 0.05)',
              border: '1px solid rgba(255, 255, 255, 0.15)', borderRadius: 30, padding: '10px 20px', width: 'min(100%, 340px)',
              transition: 'border-color 0.3s, background 0.3s',
              boxShadow: '0 4px 15px rgba(0,0,0,0.2)'
            }}>
              <span style={{ marginRight: 10, color: 'var(--text-secondary)' }}><SearchIcon /></span>
              <input 
                value={query} onChange={e => setQuery(e.target.value)}
                placeholder="Search globally for stations..."
                style={{ background: 'transparent', border: 'none', color: 'var(--text-primary)', width: '100%', outline: 'none', fontSize: 14 }}
              />
              <button onClick={startVoiceSearch} className={`voice-btn ${listening ? "listening" : ""}`} style={{ 
                  background: 'transparent', border: 'none', display: 'flex', alignItems: 'center', padding: 4, cursor: 'pointer', outline: 'none', color: listening ? 'var(--accent-tertiary)' : 'var(--text-secondary)'
                }}>
                {listening ? (
                  <div className="listening-bars">
                     <div className="v-bar"></div><div className="v-bar"></div>
                     <div className="v-bar"></div><div className="v-bar"></div>
                  </div>
                ) : <MicIcon />}
              </button>
            </div>
          </div>
        </header>

        {/* Content Area Wrap with AnimatePresence for Tab Transitions */}
        <AnimatePresence mode="wait">
          <motion.div 
            key={`${tab}-${genre}`}
            initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
            style={{ padding: '24px 40px' }}
          >
            
            {/* Hero Section (Visible on Discover tab main page) */}
            {tab === "discover" && !query && <HeroBanner />}

            {/* Integrated Nano Services directly on Discover page */}
            {tab === "discover" && !query && (
              <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} style={{ marginBottom: 40 }}>
                <h3 className="font-display" style={{ fontSize: 16, fontWeight: 700, marginBottom: 16, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: 1 }}>Premium Networks</h3>
                <div style={{ display: 'flex', gap: 16, overflowX: 'auto', paddingBottom: 16 }}>
                  {NETWORKS.map(net => (
                    <motion.button key={net.tag} onClick={() => setQuery(net.term)}
                      whileHover={{ scale: 1.05, boxShadow: `0 8px 24px ${net.shadow}` }}
                      whileTap={{ scale: 0.95 }}
                      style={{
                        padding: '16px 24px', borderRadius: 12, background: net.color, color: '#fff',
                        fontWeight: 700, fontSize: 16, border: 'none', cursor: 'pointer', flexShrink: 0, 
                        boxShadow: '0 4px 14px rgba(0,0,0,0.4)',
                      }}
                    >
                      {net.label}
                    </motion.button>
                  ))}
                </div>
              </motion.div>
            )}

            {/* Grid Framer Motion */}
            <motion.div 
              variants={containerVariant} initial="hidden" animate="show"
              style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: 24 }}
            >
              {loading || isTypingSearch ? (
                Array.from({ length: 14 }).map((_, i) => <motion.div key={`sk-${i}`} variants={itemVariant}><SkeletonCard /></motion.div>)
              ) : displayList.length === 0 ? (
                <motion.div variants={itemVariant} style={{ gridColumn: '1/-1', textAlign: 'center', color: 'var(--text-secondary)', padding: '60px 0' }}>
                  <div style={{ fontSize: 64, opacity: 0.3, marginBottom: 16 }}>📻</div>
                  <h3 style={{ fontSize: 20, color: 'var(--text-primary)', marginBottom: 8 }}>No stations found</h3>
                  <p>Try searching for a different term or exploring another category.</p>
                </motion.div>
              ) : (
                displayList.map((s) => (
                  <StationCard
                    key={s.stationuuid} station={s}
                    isPlaying={current?.stationuuid === s.stationuuid && isPlaying}
                    isLoading={current?.stationuuid === s.stationuuid && loadingStation}
                    isFav={isFav(s)} onPlay={() => playStation(s)} onFav={(e) => toggleFav(s, e)}
                  />
                ))
              )}
            </motion.div>
          </motion.div>
        </AnimatePresence>
      </main>

      {/* ── PLAYER BAR ─────────────────────────────────────── */}
      {current && (
        <motion.div 
          initial={{ y: 100 }} animate={{ y: 0 }} transition={{ type: "spring", stiffness: 300, damping: 30 }}
          className="acrylic" style={{
            position: 'fixed', bottom: 0, left: 0, right: 0, height: 'var(--player-height)',
            display: 'flex', flexDirection: 'column', padding: '0 32px', zIndex: 100,
            borderTop: '1px solid var(--border-dim)'
        }}>
          {/* Tracker Bar */}
          <div style={{ width: '100%', position: 'absolute', top: -2, left: 0, right: 0 }}>
             <div style={{ 
               height: 2, background: 'var(--accent-primary)', 
               width: `${(sessionDuration % 60) * (100/60)}%`,
               transition: 'width 1s linear',
               boxShadow: '0 0 10px var(--accent-primary)'
             }} />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', height: '100%' }}>
          {/* Track Info */}
          <div 
             className="player-info-container" 
             onClick={() => setPlayerOpen(true)}
             style={{ display: 'flex', alignItems: 'center', gap: 16, flex: 1, minWidth: 0, cursor: 'pointer' }}
          >
            <StationLogo src={current.favicon} name={current.name} size={64} isPlaying={isPlaying} />
            <div style={{ minWidth: 0 }}>
              <h4 className="font-display" style={{ fontSize: 16, fontWeight: 700, margin: '0 0 4px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {current.name}
              </h4>
              <p style={{ fontSize: 13, color: 'var(--text-secondary)', margin: 0, display: 'flex', alignItems: 'center', gap: 8 }}>
                {isPlaying && !loadingStation && <span style={{ color: 'var(--accent-primary)', fontWeight: 700, fontSize: 11 }}>● LIVE</span>}
                {loadingStation && <span style={{ color: 'var(--accent-secondary)', fontSize: 11 }}>Buffering...</span>}
                {!loadingStation && !errorStatus && <span style={{ fontSize: 12, fontWeight: 600 }}>{Math.floor(sessionDuration/60)}:{(sessionDuration%60).toString().padStart(2,'0')}</span>}
                {errorStatus && <span style={{ color: 'var(--accent-tertiary)', fontSize: 11 }}>{errorStatus}</span>}
              </p>
            </div>
            
            <button 
              className={`heart-btn ${isFav(current) ? 'active' : ''}`}
              onClick={(e) => toggleFav(current, e)} 
              title="Add to Your Library"
              style={{ background: 'transparent', border: 'none', cursor: 'pointer' }}
            >
              <HeartIcon filled={isFav(current)} />
            </button>
            
          </div>

          {/* Morphing Controls */}
          <div className="player-interaction-container" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 10, flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
              <button 
                className="play-pause-btn"
                onClick={() => {
                  const audio = audioRef.current;
                  if (!audio) return;
                  if (isPlaying) { audio.pause(); setIsPlaying(false); }
                  else { audio.play(); }
                }} style={{
                  width: 54, height: 54, borderRadius: '50%', background: 'var(--text-primary)', color: 'var(--bg-base)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 15px rgba(255,255,255,0.2)',
                  border: 'none', cursor: 'pointer'
                }}>
                {loadingStation ? <div className="spinner" style={{ borderColor: 'rgba(0,0,0,0.1)', borderTopColor: '#000' }} /> : (
                  <span key={isPlaying ? 'pause' : 'play'} className="icon-crossfade-enter" style={{ display: 'flex', alignItems: 'center' }}>
                    {isPlaying ? <PauseIcon /> : <PlayIcon />}
                  </span>
                )}
              </button>
            </div>
          </div>

          {/* Volume Extra Controls */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, flex: 1, justifyContent: 'flex-end' }}>
            {isPlaying && !loadingStation && <EqBars />}
            <button onClick={() => setMuted(!muted)} style={{ color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', background: 'transparent', border: 'none', cursor: 'pointer' }}>
              {muted || volume === 0 ? <VolumeMutedIcon /> : volume < 0.5 ? <VolumeLowIcon /> : <VolumeHighIcon />}
            </button>
            <input type="range" min="0" max="1" step="0.01" value={muted ? 0 : volume} onChange={e => { setVolume(parseFloat(e.target.value)); if(muted) setMuted(false); }} 
              style={{ width: 100 }} />
          </div>
          </div>
        </motion.div>
      )}
    </div>
  );
}

function StationCard({ station: s, isPlaying, isLoading, isFav, onPlay, onFav }) {
  return (
    <motion.div variants={itemVariant} layoutId={s.stationuuid} className="card-hover" onClick={onPlay} style={{
      background: isPlaying ? 'var(--bg-hover)' : 'var(--bg-surface)',
      border: isPlaying ? '1px solid var(--accent-primary)' : '1px solid var(--border-dim)',
      borderRadius: 24, padding: 18, cursor: 'pointer', position: 'relative', overflow: 'hidden',
      display: 'flex', flexDirection: 'column', height: '100%',
      boxShadow: isPlaying ? '0 8px 30px rgba(145, 70, 255, 0.15)' : 'none'
    }}>
      {isPlaying && (
        <div style={{
          position: 'absolute', top: -50, right: -50, width: 150, height: 150, borderRadius: '50%',
          background: 'radial-gradient(circle, rgba(145, 70, 255, 0.15) 0%, transparent 70%)', pointerEvents: 'none'
        }} />
      )}
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
        <StationLogo src={s.favicon} name={s.name} size={72} isPlaying={isPlaying} />
        <button 
          className={`heart-btn ${isFav ? 'active' : ''}`} 
          onClick={(e) => { e.stopPropagation(); onFav(e); }} 
          style={{ zIndex: 2, background: 'transparent', border: 'none', cursor: 'pointer' }}
        >
          <HeartIcon filled={isFav} />
        </button>
      </div>

      <h3 className="font-display" style={{ fontSize: 16, fontWeight: 700, margin: '0 0 6px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
        {s.name}
      </h3>

      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 'auto' }}>
        <span style={{ fontSize: 12, color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: 4 }}>
          {countryFlag(s.countrycode)} {s.country || "Global"}
        </span>
        {s.tags && s.tags.split(",").slice(0, 1).map(t => t.trim()).filter(Boolean).map(tag => (
          <span key={tag} style={{
            fontSize: 10, padding: '2px 8px', borderRadius: 12, background: 'var(--bg-base)', color: 'var(--text-secondary)', textTransform: 'capitalize'
          }}>{tag}</span>
        ))}
      </div>
    </motion.div>
  );
}
