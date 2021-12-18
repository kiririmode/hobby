#!/bin/bash

set -euo pipefail

export TF_PLUGIN_CACHE_DIR="$HOME/.terraform.d/plugin-cache"
mkdir -p $TF_PLUGIN_CACHE_DIR

readonly FS_MIRROR="$HOME/.terraform.d/mirror"
terraform providers mirror \
    -platform=darwin_amd64 \
    -platform=linux_amd64 \
    ${FS_MIRROR}

DIRS=$(find . -name \*.tf -exec dirname {} \; | sort | uniq)
for d in $DIRS; do
    echo $d
    pushd $d
    rm -f .terraform.lock.hcl

    # terraform providers lock は、TF_PLUGIN_CACHE を見ることなく registry と
    # 通信するため、fs-mirror 指定をしないと時間がかかってしまう
    terraform providers lock \
        -fs-mirror=${FS_MIRROR} \
        -platform=darwin_amd64 \
        -platform=linux_amd64
    popd
done
