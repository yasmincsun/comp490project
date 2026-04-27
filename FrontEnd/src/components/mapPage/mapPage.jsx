import React, { useRef, useEffect, useState } from "react";
import * as maptilersdk from "@maptiler/sdk";
import { GeocodingControl } from "@maptiler/geocoding-control/maptilersdk";
import Box from "@mui/material/Box";

import "@maptiler/sdk/dist/maptiler-sdk.css";
import "./mapPage.css";

import configData from "./mapConfig";

const API_BASE_URL = "http://127.0.0.1:8080";

// Styles list
const baseMaps = {
  STREETS_V4: {
    img: "https://cloud.maptiler.com/static/img/maps/streets.png",
  },
  OPENSTREETMAP: {
    img: "https://cloud.maptiler.com/static/img/maps/openstreetmap.png",
  },
  HYBRID_V4: {
    img: "https://cloud.maptiler.com/static/img/maps/hybrid.png",
  },
};

class LayerSwitcherControl {
  constructor(options) {
    this._options = { ...options };
    this._container = document.createElement("div");
    this._container.classList.add(
      "maplibregl-ctrl",
      "maplibregl-ctrl-basemaps",
      "closed"
    );

    const dir = this._options.expandDirection || "right";
    if (dir === "top" || dir === "left") this._container.classList.add("reverse");
    if (dir === "top" || dir === "down") this._container.classList.add("column");
    else this._container.classList.add("row");

    this._container.addEventListener("mouseenter", () => {
      this._container.classList.remove("closed");
    });

    this._container.addEventListener("mouseleave", () => {
      this._container.classList.add("closed");
    });
  }

  onAdd(map) {
    this._map = map;

    const basemaps = this._options.basemaps;
    const initialId = this._options.initialBasemapId;

    Object.keys(basemaps).forEach((layerId) => {
      const base = basemaps[layerId];

      const img = document.createElement("img");
      img.src = base.img;
      img.classList.add("basemap");
      img.dataset.id = layerId;

      img.addEventListener("click", () => {
        const active = this._container.querySelector(".active");
        if (active) active.classList.remove("active");
        img.classList.add("active");

        this._map.setStyle(maptilersdk.MapStyle[layerId]);
      });

      if (layerId === initialId) img.classList.add("active");
      this._container.appendChild(img);
    });

    return this._container;
  }

  onRemove() {
    this._container.parentNode?.removeChild(this._container);
    delete this._map;
  }
}

export default function MapPage() {
  const mapContainer = useRef(null);
  const map = useRef(null);
  const eventMarkersRef = useRef([]);
  const keywordRef = useRef("");
  const geocodeCacheRef = useRef(new Map());

  const center = { lng: -118.374107, lat: 34.1021 };

  const [zoom] = useState(4.2);
  const [keyword, setKeyword] = useState("");
  const [statusMsg, setStatusMsg] = useState("");
  const [selectedEvent, setSelectedEvent] = useState(null);

  keywordRef.current = keyword;
  maptilersdk.config.apiKey = configData.MAPTILER_API_KEY;

  function clearEventMarkers() {
    eventMarkersRef.current.forEach((marker) => marker.remove());
    eventMarkersRef.current = [];
  }

  function getEventImage(ev) {
    const images = Array.isArray(ev?.images) ? ev.images : [];
    if (!images.length) return "";

    const sixteenByNine = images.filter((img) => img?.ratio === "16_9" && img?.url);
    const candidates = sixteenByNine.length
      ? sixteenByNine
      : images.filter((img) => img?.url);

    candidates.sort((a, b) => (b?.width || 0) - (a?.width || 0));

    return candidates[0]?.url || "";
  }

  function formatEventDateTime(ev) {
    const start = ev?.dates?.start;
    if (!start) return "Time unavailable";

    const localDate = start?.localDate || "";
    const localTime = start?.localTime || "";

    if (localDate && localTime) return `${localDate} ${localTime}`;
    if (localDate) return localDate;
    if (localTime) return localTime;

    return "Time unavailable";
  }

  function formatVenueAddress(ev) {
    const venue = ev?._embedded?.venues?.[0];
    if (!venue) return "Address unavailable";

    const parts = [
      venue?.address?.line1,
      venue?.city?.name,
      venue?.state?.stateCode || venue?.state?.name,
      venue?.postalCode,
      venue?.country?.name,
    ].filter(Boolean);

    return parts.length ? parts.join(", ") : "Address unavailable";
  }

  function buildVenueAddress(venue) {
    return [
      venue?.address?.line1,
      venue?.city?.name,
      venue?.state?.stateCode || venue?.state?.name,
      venue?.postalCode,
      venue?.country?.name,
    ]
      .filter(Boolean)
      .join(", ");
  }

  async function geocodeVenueAddress(venue) {
    const address = buildVenueAddress(venue);
    if (!address) return null;

    const cached = geocodeCacheRef.current.get(address);
    if (cached) return cached;

    try {
      const query = encodeURIComponent(address);
      const url = `https://api.maptiler.com/geocoding/${query}.json?key=${configData.MAPTILER_API_KEY}&limit=1`;

      const res = await fetch(url);
      if (!res.ok) return null;

      const data = await res.json();
      const centerCoords = data?.features?.[0]?.center;

      if (!Array.isArray(centerCoords) || centerCoords.length < 2) return null;

      const [lng, lat] = centerCoords;

      if (!Number.isFinite(lat) || !Number.isFinite(lng)) return null;

      const coords = { lat, lng };
      geocodeCacheRef.current.set(address, coords);
      return coords;
    } catch (error) {
      console.error("Address geocoding failed:", error);
      return null;
    }
  }

  async function resolveEventCoordinates(ev) {
    const venue = ev?._embedded?.venues?.[0];
    if (!venue) return null;

    const geocodedCoords = await geocodeVenueAddress(venue);
    if (geocodedCoords) return geocodedCoords;

    const lat = Number(venue?.location?.latitude);
    const lng = Number(venue?.location?.longitude);

    if (!Number.isFinite(lat) || !Number.isFinite(lng)) return null;

    return { lat, lng };
  }

  async function addEventsToMap(events) {
    if (!map.current) return;

    clearEventMarkers();
    setSelectedEvent(null);

    const bounds = new maptilersdk.LngLatBounds();
    let markerCount = 0;

    const resolvedEvents = await Promise.all(
      events.map(async (ev) => {
        const coords = await resolveEventCoordinates(ev);
        return { ev, coords };
      })
    );

    for (const { ev, coords } of resolvedEvents) {
      if (!coords) continue;

      const { lat, lng } = coords;

      const marker = new maptilersdk.Marker()
        .setLngLat([lng, lat])
        .addTo(map.current);

      marker.getElement().addEventListener("click", () => {
        setSelectedEvent(ev);

        map.current.flyTo({
          center: [lng, lat],
          zoom: Math.max(map.current.getZoom(), 10),
        });
      });

      eventMarkersRef.current.push(marker);
      bounds.extend([lng, lat]);
      markerCount += 1;
    }

    if (markerCount === 1) {
      const marker = eventMarkersRef.current[0];
      const lngLat = marker.getLngLat();

      map.current.flyTo({
        center: [lngLat.lng, lngLat.lat],
        zoom: 10,
      });
    } else if (markerCount > 1) {
      map.current.fitBounds(bounds, {
        padding: 60,
        maxZoom: 10,
      });
    }
  }

  async function searchEventsFromBackend({ keyword: kw = "", lat = null, lng = null }) {
    setStatusMsg("Searching events...");

    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        throw new Error("You are not logged in.");
      }

      const params = new URLSearchParams();

      if (kw.trim()) {
        params.set("keyword", kw.trim());
      }

      if (lat !== null && lng !== null) {
        params.set("lat", String(lat));
        params.set("lng", String(lng));
      }

      const res = await fetch(`${API_BASE_URL}/api/v1/map/search?${params.toString()}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const rawText = await res.text();

      if (!res.ok) {
        throw new Error(`Backend returned ${res.status}: ${rawText}`);
      }

      const events = rawText ? JSON.parse(rawText) : [];
      const safeEvents = Array.isArray(events) ? events : [];

      setStatusMsg(
        `${safeEvents.length} events found${kw.trim() ? ` for "${kw.trim()}"` : ""}.`
      );

      await addEventsToMap(safeEvents);
      return safeEvents;
    } catch (error) {
      console.error("Backend map search failed:", error);
      setStatusMsg(error.message || "Could not load events.");
      clearEventMarkers();
      setSelectedEvent(null);
      return [];
    }
  }

  function runKeywordSearch(useMapCenter = false) {
    if (!keyword.trim()) {
      setStatusMsg("Enter a keyword to search.");
      return;
    }

    if (!useMapCenter) {
      searchEventsFromBackend({ keyword });
      return;
    }

    if (!map.current) return;

    const currentCenter = map.current.getCenter();

    searchEventsFromBackend({
      keyword,
      lat: currentCenter.lat,
      lng: currentCenter.lng,
    });
  }

  function getLocationAndEvents() {
    if (!navigator.geolocation) {
      setStatusMsg("Geolocation is not supported by this browser.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;

        if (map.current) {
          map.current.flyTo({ center: [lng, lat], zoom: 8 });
        }
      },
      (err) => {
        console.error(err);
        setStatusMsg("Location permission denied or unavailable.");
      }
    );
  }

  async function runAISearch() {
    setStatusMsg("AI searching concerts...");

    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        throw new Error("You are not logged in.");
      }

      if (!keyword.trim()) {
        throw new Error("Enter something to search.");
      }

      const params = new URLSearchParams({
        prompt: keyword.trim(),
      });

      const res = await fetch(`${API_BASE_URL}/api/v1/map/ai-search?${params.toString()}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const rawText = await res.text();

      if (!res.ok) {
        throw new Error(`Backend returned ${res.status}: ${rawText}`);
      }

      const events = rawText ? JSON.parse(rawText) : [];
      const safeEvents = Array.isArray(events) ? events : [];

      setStatusMsg(`${safeEvents.length} AI events found for "${keyword.trim()}".`);

      await addEventsToMap(safeEvents);
      return safeEvents;
    } catch (error) {
      console.error("AI map search failed:", error);
      setStatusMsg(error.message || "Could not run AI search.");
      clearEventMarkers();
      setSelectedEvent(null);
      return [];
    }
  }

  useEffect(() => {
    if (map.current) return;

    const initialBasemapId = "STREETS_V4";

    map.current = new maptilersdk.Map({
      container: mapContainer.current,
      style: maptilersdk.MapStyle[initialBasemapId],
      center: [center.lng, center.lat],
      zoom,
      projectionControl: true,
    });

    const geocoder = new GeocodingControl();
    map.current.addControl(geocoder, "top-left");

    geocoder.on("result", async (e) => {
      const [lng, lat] = e.result.center;

      map.current.flyTo({ center: [lng, lat], zoom: 8 });

      if (keywordRef.current.trim()) {
        await searchEventsFromBackend({
          keyword: keywordRef.current,
          lat,
          lng,
        });
      }
    });

    map.current.addControl(
      new LayerSwitcherControl({
        basemaps: baseMaps,
        initialBasemapId,
        expandDirection: "right",
      }),
      "bottom-left"
    );

    map.current.on("load", () => {
      getLocationAndEvents();
    });

    return () => {
      clearEventMarkers();

      if (map.current) {
        map.current.remove();
        map.current = null;
      }
    };
  }, [center.lng, center.lat, zoom]);

  return (
    <Box sx={{ display: "flex" }}>
      <div className="container" style={{ position: "relative" }}>
        <div className={`event-drawer ${selectedEvent ? "open" : ""}`}>
          {selectedEvent && (
            <>
              <button
                className="event-drawer-close"
                onClick={() => setSelectedEvent(null)}
                aria-label="Close event details"
              >
                ×
              </button>

              {getEventImage(selectedEvent) ? (
                <img
                  src={getEventImage(selectedEvent)}
                  alt={selectedEvent?.name || "Event"}
                  className="event-drawer-image"
                />
              ) : (
                <div className="event-drawer-image placeholder">
                  No image available
                </div>
              )}

              <div className="event-drawer-content">
                <h2>{selectedEvent?.name || "Event"}</h2>

                <p>
                  <strong>When:</strong> {formatEventDateTime(selectedEvent)}
                </p>

                <p>
                  <strong>Venue:</strong>{" "}
                  {selectedEvent?._embedded?.venues?.[0]?.name || "Venue unavailable"}
                </p>

                <p>
                  <strong>Address:</strong> {formatVenueAddress(selectedEvent)}
                </p>

                {selectedEvent?.info && (
                  <p>
                    <strong>Info:</strong> {selectedEvent.info}
                  </p>
                )}

                {selectedEvent?.url && (
                  <a
                    href={selectedEvent.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="event-link"
                  >
                    View event on Ticketmaster
                  </a>
                )}
              </div>
            </>
          )}
        </div>

        <div
          style={{
            position: "absolute",
            top: 12,
            right: 12,
            zIndex: 2,
            display: "flex",
            gap: 8,
            alignItems: "center",
            background: "rgba(255,255,255,0.92)",
            padding: 10,
            borderRadius: 10,
          }}
        >
          <input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder='Keyword (e.g., "jazz", "drake")'
            style={{
              width: 240,
              padding: "8px 10px",
              borderRadius: 8,
              border: "1px solid #ccc",
              outline: "none",
            }}
          />

          <button
            onClick={() => runKeywordSearch(false)}
            style={{ padding: "8px 10px", borderRadius: 8, cursor: "pointer" }}
          >
            Search
          </button>

          <button
            onClick={runAISearch}
            style={{ padding: "8px 10px", borderRadius: 8, cursor: "pointer" }}
          >
            AI Search
          </button>

          <button
            onClick={() => runKeywordSearch(true)}
            style={{ padding: "8px 10px", borderRadius: 8, cursor: "pointer" }}
            title="Search using the current map center"
          >
            Use map center
          </button>
        </div>

        {statusMsg && (
          <div
            style={{
              position: "absolute",
              top: 62,
              right: 12,
              zIndex: 2,
              background: "rgba(255,255,255,0.92)",
              padding: "6px 10px",
              borderRadius: 10,
              maxWidth: 420,
              fontSize: 13,
              whiteSpace: "pre-wrap",
            }}
          >
            {statusMsg}
          </div>
        )}

        <div ref={mapContainer} id="map" className="map" />
      </div>
    </Box>
  );
}