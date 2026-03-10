<template>
  <IFrame :src="src" />
</template>

<script setup lang="ts" name="Swagger">
const src = ref(import.meta.env.VITE_APP_SWAGGER_URL)
</script>
