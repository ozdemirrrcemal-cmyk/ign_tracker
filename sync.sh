#!/bin/sh
set -e

cd "$(dirname "$0")"

MESSAGE="$1"

if [ -z "$MESSAGE" ]; then
  MESSAGE="AndroidIDE update"
fi

echo "== Proje klasörü =="
pwd

echo "== Git durumu =="
git status --short

if [ -n "$(git status --porcelain)" ]; then
  git add .
  git commit -m "$MESSAGE"
else
  echo "Commitlenecek değişiklik yok."
fi

git push origin main

echo "== GitHub push tamamlandı =="
