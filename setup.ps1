# # Create folder structure
# $dirs = @(
#     "common/src/main/java/com/aatzhmair/common/config",
#     "common/src/main/java/com/aatzhmair/common/controller",
#     "common/src/main/java/com/aatzhmair/common/dto",
#     "common/src/main/java/com/aatzhmair/common/entity",
#     "common/src/main/java/com/aatzhmair/common/exception",
#     "common/src/main/java/com/aatzhmair/common/repository",
#     "common/src/main/java/com/aatzhmair/common/search",
#     "common/src/main/java/com/aatzhmair/common/security",
#     "common/src/main/java/com/aatzhmair/common/service",
#     "common/src/main/resources/db/migration",
#     "common/src/test/java/com/aatzhmair/common/entity"
# )
# foreach ($dir in $dirs) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
#
# # Create empty placeholder files
# $files = @(
#     "common/pom.xml",
#     "common/Dockerfile",
#     "common/src/main/java/com/aatzhmair/common/commonApplication.java",
#     "common/src/main/java/com/aatzhmair/common/config/CacheConfig.java",
#     "common/src/main/java/com/aatzhmair/common/config/SecurityConfig.java",
#     "common/src/main/java/com/aatzhmair/common/controller/ProductController.java",
#     "common/src/main/java/com/aatzhmair/common/dto/ProductRequest.java",
#     "common/src/main/java/com/aatzhmair/common/dto/ProductResponse.java",
#     "common/src/main/java/com/aatzhmair/common/dto/StockAdjustmentRequest.java",
#     "common/src/main/java/com/aatzhmair/common/entity/Product.java",
#     "common/src/main/java/com/aatzhmair/common/exception/ProductNotFoundException.java",
#     "common/src/main/java/com/aatzhmair/common/exception/InsufficientStockException.java",
#     "common/src/main/java/com/aatzhmair/common/exception/GlobalExceptionHandler.java",
#     "common/src/main/java/com/aatzhmair/common/repository/ProductRepository.java",
#     "common/src/main/java/com/aatzhmair/common/search/ProductDocument.java",
#     "common/src/main/java/com/aatzhmair/common/search/ProductSearchRepository.java",
#     "common/src/main/java/com/aatzhmair/common/search/ProductSearchService.java",
#     "common/src/main/java/com/aatzhmair/common/security/JwtService.java",
#     "common/src/main/java/com/aatzhmair/common/security/JwtAuthFilter.java",
#     "common/src/main/java/com/aatzhmair/common/service/common.java",
#     "common/src/main/resources/application.yml",
#     "common/src/main/resources/db/migration/V1__init_product_schema.sql",
#     "common/src/test/java/com/aatzhmair/common/entity/ProductTest.java"
# )
# foreach ($file in $files) { if (!(Test-Path $file)) { Set-Content -Path $file -Value "" } }
# Write-Host "common structure created successfully!" -ForegroundColor Green

# Create folder structure
$dirs = @(
    "common/src/main/java/com/aatzhmair/common/security",
    "common/src/main/java/com/aatzhmair/common/events",
    "common/src/main/resources"
)
foreach ($dir in $dirs) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }

# Create empty placeholder files
$files = @(
    "common/pom.xml",
    "common/Dockerfile",
    "common/src/main/java/com/aatzhmair/common/commonApplication.java",
    "common/src/main/java/com/aatzhmair/common/events/OrderPlacedEvent.java",
    "common/src/main/java/com/aatzhmair/common/events/ProductChangedEvent.java",
    "common/src/main/java/com/aatzhmair/common/security/AuthenticatedUser.java",
    "common/src/main/resources/application.yml"
)
foreach ($file in $files) { if (!(Test-Path $file)) { Set-Content -Path $file -Value "" } }
Write-Host "common structure created successfully!" -ForegroundColor Green