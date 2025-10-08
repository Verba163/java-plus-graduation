#!/bin/sh
set -e

host=$1
port=$2
path=$3

until curl -sf "http://${host}:${port}${path}"; do
  echo "Waiting for ${host}:${port}${path} ..."
  sleep 3
done