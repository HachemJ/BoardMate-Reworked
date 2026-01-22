<template>
  <div class="event-card" :class="{ incomplete: !!preview && !isComplete }">
    <div class="hero" :style="heroStyle">
      <div class="hero-overlay"></div>
      <div class="hero-badges">
        <span class="badge value" :class="{ filled: hasDate }">{{ dateLabel }}</span>
        <span class="badge value" :class="{ filled: hasSpots, full: isFull }">{{ spotsLabel }}</span>
        <span v-if="statusLabel" class="badge status" :class="statusClass">{{ statusLabel }}</span>
      </div>
      <div class="hero-letter">{{ heroLetter }}</div>
    </div>

    <div class="body">
      <div class="title value" :class="{ filled: hasName }">{{ name || "Event name" }}</div>
      <div class="sub value" :class="{ filled: hasBoardGame }">{{ boardGameName || "Board game" }}</div>

      <div class="info">
        <div class="info-item value" :class="{ filled: hasTime }">
          <svg class="info-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 2a10 10 0 1 0 10 10A10.011 10.011 0 0 0 12 2zm1 11h4v-2h-3V7h-2v6z" fill="currentColor"/>
          </svg>
          <span>{{ timeLabel }}</span>
        </div>
        <div class="info-item value" :class="{ filled: hasLocation }">
          <svg class="info-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 2C8.686 2 6 4.686 6 8c0 4.418 6 12 6 12s6-7.582 6-12c0-3.314-2.686-6-6-6zm0 8.5a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5z" fill="currentColor"/>
          </svg>
          <span>{{ locationLabel }}</span>
        </div>
      </div>

      <div class="desc">
        <span class="label">Description</span>
        <div class="desc-text value" :class="{ filled: hasDescription }">
          {{ descriptionLabel }}
        </div>
      </div>
    </div>

    <div v-if="$slots.footer" class="footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  event: { type: Object, required: true },
  preview: { type: Boolean, default: false },
});

const name = computed(() => props.event?.name || "");
const boardGameName = computed(() => props.event?.boardGameName || "");
const dateRaw = computed(() => props.event?.date || "");
const startTime = computed(() => props.event?.startTime || "");
const endTime = computed(() => props.event?.endTime || "");
const location = computed(() => props.event?.location || "");
const description = computed(() => props.event?.description || "");
const spotsCurrent = computed(() => Number(props.event?.spotsCurrent ?? 0));
const spotsMax = computed(() => Number(props.event?.spotsMax ?? 0));
const statusLabel = computed(() => props.event?.status || "");
const heroImageUrl = computed(() => props.event?.imageUrl || "");

const hasName = computed(() => !!name.value);
const hasBoardGame = computed(() => !!boardGameName.value);
const hasDate = computed(() => !!dateRaw.value);
const hasTime = computed(() => !!startTime.value && !!endTime.value);
const hasLocation = computed(() => !!location.value);
const hasDescription = computed(() => !!description.value);
const hasSpots = computed(() => spotsMax.value > 0);

const isComplete = computed(() =>
  hasName.value &&
  hasBoardGame.value &&
  hasDate.value &&
  hasTime.value &&
  hasLocation.value &&
  hasDescription.value &&
  hasSpots.value
);

const isFull = computed(() => spotsMax.value > 0 && spotsCurrent.value >= spotsMax.value);

const dateLabel = computed(() => {
  if (!dateRaw.value) return "Date TBD";
  const [y, m, d] = dateRaw.value.split("-").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1);
  if (Number.isNaN(dt.getTime())) return "Date TBD";
  return dt.toLocaleDateString(undefined, { month: "short", day: "2-digit", year: "numeric" });
});

function fmtTime(hhmm) {
  if (!hhmm) return "";
  const short = hhmm.length >= 5 ? hhmm.slice(0, 5) : hhmm;
  const [h, m] = short.split(":").map(Number);
  const t = new Date();
  t.setHours(h || 0, m || 0, 0, 0);
  return t.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

const timeLabel = computed(() => {
  if (!startTime.value || !endTime.value) return "Time TBD";
  return `${fmtTime(startTime.value)}–${fmtTime(endTime.value)}`;
});

const locationLabel = computed(() => (location.value?.trim() ? location.value : "Location TBD"));
const descriptionLabel = computed(() =>
  description.value?.trim() ? description.value : "Add a short description..."
);

const spotsLabel = computed(() => {
  if (!spotsMax.value) return "Spots TBD";
  return `${spotsCurrent.value} / ${spotsMax.value} spots`;
});

const heroStyle = computed(() => {
  if (heroImageUrl.value) {
    return {
      backgroundImage: `linear-gradient(rgba(10,12,16,0.35), rgba(10,12,16,0.65)), url(${heroImageUrl.value})`,
      backgroundSize: "cover",
      backgroundPosition: "center",
    };
  }
  return {
    backgroundImage: "radial-gradient(circle at 20% 20%, rgba(120,140,255,0.18), transparent 45%), linear-gradient(135deg, #141a22, #0f1217)",
  };
});

const heroLetter = computed(() => (boardGameName.value || "Game").slice(0, 1));
const statusClass = computed(() => statusLabel.value?.toLowerCase?.() || "");
</script>

<style scoped>
.event-card {
  border: 1px solid #1f2533;
  border-radius: 16px;
  background: #0f1217;
  color: #e9edf5;
  padding: 16px;
  display: grid;
  gap: 12px;
}
.event-card.incomplete { border-color: #2a303d; }

.hero {
  height: 160px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  position: relative;
  overflow: hidden;
  border: 1px solid #1f2533;
  color: #e6ecff;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(12,14,18,0.1), rgba(12,14,18,0.75));
}
.hero-badges {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 8px;
  z-index: 2;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.hero-letter { font-weight: 900; font-size: 28px; z-index: 1; }

.badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #2f384a;
  color: #dfe5f4;
  font-weight: 700;
  font-size: 12px;
  background: rgba(16,20,28,0.65);
}
.badge.full { border-color: #6a4a23; color: #ffe2b8; }
.badge.status.upcoming { border-color: #2e4a74; color: #cde6ff; }
.badge.status.ongoing { border-color: #2c5b38; color: #c8ffd5; }
.badge.status.finished { border-color: #5b2c2c; color: #ffd6d6; }

.body { display: grid; gap: 8px; }
.title { font-weight: 900; font-size: 20px; color: #ffffff; }
.sub { color: #b6becc; }
.info { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.info-item { display: inline-flex; align-items: center; gap: 8px; color: #dfe5f4; font-weight: 700; font-size: 13px; }
.info-icon { width: 16px; height: 16px; opacity: 0.85; }

.desc { display: grid; gap: 4px; }
.label { opacity: 0.7; font-size: 12px; }
.desc-text {
  color: #c6cedf;
  font-size: 13px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.footer { display: flex; justify-content: space-between; gap: 12px; }

.value { opacity: 0.7; transform: translateY(4px); transition: opacity .25s ease, transform .25s ease; }
.value.filled { opacity: 1; transform: translateY(0); }

@media (max-width: 900px) {
  .info { grid-template-columns: 1fr; }
}
</style>
