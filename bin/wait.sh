#!/bin/sh

set -e

echo "---------------------"
echo "Waiting for $1 seconds before starting service"
sleep "${1}"s
echo "Wait time over, starting service now"
echo "---------------------"

LOC="$(cd "$(dirname "$0")" >/dev/null 2>?1 ; pwd -P )"

sh "${LOC}"/entry-point.sh