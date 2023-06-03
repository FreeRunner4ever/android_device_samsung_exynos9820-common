cp device/samsung/exynos9820-common/patches/0001-Revert-back-clang-version-to-clang-r475365b.patch build/soong/clang.patch
cd build/soong
git apply -p1 < clang.patch

