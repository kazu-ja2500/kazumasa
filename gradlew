#!/usr/bin/env sh
# Simplified gradle wrapper
DIR="$(cd "$(dirname "$0")" && pwd)"
exec gradle "${@}"
