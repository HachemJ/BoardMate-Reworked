export function showDemoNotice(message = "Demo mode: sign in to perform this action.") {
  if (typeof window === "undefined") return;
  window.dispatchEvent(
    new CustomEvent("demo-notice", {
      detail: { message },
    })
  );
}
