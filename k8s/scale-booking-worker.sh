#!/usr/bin/env bash

set -euo pipefail

NAMESPACE="${NAMESPACE:-default}"
DEPLOYMENT_NAME="${DEPLOYMENT_NAME:-booking-worker}"
REPLICAS="${1:-3}"

if ! command -v kubectl >/dev/null 2>&1; then
  echo "kubectl is required but was not found in PATH" >&2
  exit 1
fi

if ! [[ "$REPLICAS" =~ ^[0-9]+$ ]]; then
  echo "replicas must be a non-negative integer" >&2
  exit 1
fi

echo "Scaling deployment '$DEPLOYMENT_NAME' in namespace '$NAMESPACE' to $REPLICAS replicas"
kubectl scale deployment "$DEPLOYMENT_NAME" \
  --namespace "$NAMESPACE" \
  --replicas "$REPLICAS"

echo "Waiting for rollout to finish"
kubectl rollout status deployment/"$DEPLOYMENT_NAME" \
  --namespace "$NAMESPACE" \
  --timeout=180s

echo "Current worker pods"
kubectl get pods \
  --namespace "$NAMESPACE" \
  --selector "app=booking-worker" \
  --output wide
