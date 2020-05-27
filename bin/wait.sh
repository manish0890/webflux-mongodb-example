#!/bin/sh

set -e

echo "---------------------"
echo "Waiting for $1 seconds before starting service"
sleep "${1}"s
echo "Wait time over, starting service now"
echo "---------------------"

BIN_DIR="$(cd "$(dirname "$0")" >/dev/null 2>?1 ; pwd -P )"

sh "${BIN_DIR}"/entry-point.sh