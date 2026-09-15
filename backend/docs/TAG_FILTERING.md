# Tag Filtering System Documentation

## Overview

The tag filtering system allows users to exclude products based on tags (e.g., dietary restrictions, allergens). When a user selects tags to exclude, the system returns only products that do **not** have any of those tags. This is particularly useful for filtering out allergens or dietary restrictions.

## Architecture

The system follows a layered architecture:

1. **Presentation Layer** - REST Controllers with Swagger documentation
2. **Service Layer** - Business logic for filtering and data enrichment
3. **Repository Layer** - Data access with custom queries
4. **Database Layer** - PostgreSQL with many-to-many relationships

### Key Components

- **ProductController**: Handles product CRUD and filtering
- **ProductTagController**: Manages product-tag relationships
- **TagController**: Manages tag CRUD operations
- **ProductService**: Business logic for product filtering
- **ProductTagRepository**: Custom queries for tag-based filtering

## Database Schema

The system uses a many-to-many relationship between products and tags:

```
product (1) ────< (many) product_tag (many) >──── (1) tag
```

- **product**: Main product table
- **tag**: Tag definitions (e.g., "Gluten", "Dairy", "Vegan")
- **product_tag**: Junction table linking products to tags

See [database-schema.puml](diagram/database-schema.puml) for detailed schema diagram.

## API Endpoints

### Get Products with Tag Exclusion

```
GET /products?excludeTagIds=1,2,3
```

**Description**: Retrieves all products, excluding those that have any of the specified tags.

**Query Parameters**:
- `excludeTagIds` (optional): Comma-separated list of tag IDs to exclude

**Response**: Array of `ProductResponse` objects, each including:
- Product details (id, name, price, etc.)
- `tags`: Array of tags associated with the product

**Example**:
```bash
# Get all products
GET /products

# Get products excluding tags 1, 2, and 3
GET /products?excludeTagIds=1,2,3
```

### Get All Tags

```
GET /tags
```

**Description**: Retrieves all available tags for the filter bar.

**Response**: Array of `TagResponse` objects with:
- `id`: Tag ID
- `name`: Tag name (e.g., "Gluten-Free")
- `svgIcon`: Optional SVG icon
- `createdAt`, `updatedAt`: Timestamps

### Product-Tag Relationship Management

All product-tag relationship endpoints are in the `ProductTagController`:

- `GET /product-tags/product/{productId}/tags` - Get tags for a product
- `POST /product-tags/product/{productId}/tag/{tagId}` - Add tag to product
- `DELETE /product-tags/product/{productId}/tag/{tagId}` - Remove tag from product

See [api-endpoints.puml](diagram/api-endpoints.puml) for complete endpoint overview.

## How It Works

### 1. Initial Load

When the user opens the menu page:

1. Frontend calls `GET /tags` to fetch all available tags
2. Frontend displays tags in a filter bar
3. Frontend calls `GET /products` to fetch all products (with tags included)

### 2. User Selects Tags to Exclude

When a user clicks a tag (e.g., "Gluten"):

1. Frontend adds the tag ID to an exclusion list: `[1, 2, 3]`
2. Frontend calls `GET /products?excludeTagIds=1,2,3`

### 3. Server-Side Filtering

The backend processes the request:

1. **ProductController** parses the `excludeTagIds` parameter
2. **ProductService** calls `findAllExcludingTagIds([1, 2, 3])`
3. **ProductTagRepository** executes SQL query:
   ```sql
   SELECT DISTINCT p.* 
   FROM product p 
   WHERE p.product_id NOT IN (
     SELECT pt.product_id 
     FROM product_tag pt 
     WHERE pt.tag_id IN (1, 2, 3)
   )
   ```
4. Products are enriched with their tags
5. Response includes products without tags 1, 2, or 3

### 4. Response Enrichment

Each product in the response includes its associated tags:

```json
{
  "id": 5,
  "name": "Caesar Salad",
  "price": 12.50,
  "tags": [
    {
      "id": 4,
      "name": "Vegetarian",
      "svgIcon": "..."
    }
  ]
}
```

This eliminates the need for additional API calls to fetch tags per product.

See [tag-filtering-data-flow.puml](diagram/tag-filtering-data-flow.puml) for detailed sequence diagram.

## Filtering Logic

### Exclusion Logic

The system uses **exclusion filtering**:
- Products that have **ANY** of the selected tags are **excluded**
- Products that have **NONE** of the selected tags are **included**

**Example**:
- Tag 1 = "Gluten"
- Tag 2 = "Dairy"
- Selected: `[1, 2]`

**Result**: Returns products that are:
- ✅ Gluten-free AND dairy-free
- ❌ NOT products with gluten
- ❌ NOT products with dairy
- ❌ NOT products with both

### SQL Query Explanation

The exclusion query works as follows:

```sql
-- Step 1: Find all product IDs that have the excluded tags
SELECT pt.product_id 
FROM product_tag pt 
WHERE pt.tag_id IN (1, 2, 3)

-- Step 2: Exclude those products from the main query
SELECT DISTINCT p.* 
FROM product p 
WHERE p.product_id NOT IN (
  -- Subquery from step 1
)
```

This ensures efficient filtering at the database level.

## Performance Considerations

### Optimizations

1. **Server-Side Filtering**: All filtering happens in the database, reducing data transfer
2. **Tags in Response**: Tags are included in product responses, eliminating N+1 query problems
3. **Single Query**: Exclusion filtering uses a single SQL query with a subquery
4. **Indexed Relationships**: The `product_tag` junction table uses composite primary key for fast lookups

### Database Indexes

The following indexes optimize tag filtering:

- `product_tag(product_id, tag_id)` - Composite primary key
- `product_tag(tag_id)` - For tag-based queries
- `product(product_id)` - Primary key index

## Frontend Integration

### Data Structures

**ProductResponse** (includes tags):
```typescript
{
  id: number;
  name: string;
  price: number;
  description: string | null;
  imageUrl: string;
  isAvailable: boolean;
  categoryId: number | null;
  createdAt: string;
  updatedAt: string;
  tags: TagResponse[] | null;  // NEW: Array of tags
}
```

**TagResponse**:
```typescript
{
  id: number;
  name: string;
  svgIcon: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}
```

### Implementation Example

```typescript
// 1. Fetch all tags for filter bar
const tags = await fetch('/api/tags').then(r => r.json());

// 2. User selects tags to exclude
const selectedTagIds = [1, 2, 3]; // e.g., Gluten, Dairy, Nuts

// 3. Fetch filtered products
const products = await fetch(
  `/api/products?excludeTagIds=${selectedTagIds.join(',')}`
).then(r => r.json());

// 4. Products already include tags - no extra API calls needed
products.forEach(product => {
  console.log(`${product.name}: ${product.tags?.map(t => t.name).join(', ')}`);
});
```

## Swagger Documentation

All endpoints are documented with Swagger/OpenAPI annotations:

- **@Tag**: Groups endpoints in Swagger UI
- **@Operation**: Describes each endpoint
- **@Parameter**: Documents path and query parameters

Access Swagger UI at: `http://localhost:8080/swagger-ui`

### Endpoint Groups

- **Products**: Product CRUD and filtering
- **Product Tags**: Product-tag relationship management
- **Tags**: Tag CRUD operations

## Error Handling

### Invalid Tag IDs

If invalid tag IDs are provided in `excludeTagIds`:
- Invalid IDs are filtered out (using `toIntOrNull()`)
- Only valid tag IDs are used for filtering
- Empty list results in all products being returned

### Missing Products

If a product doesn't exist:
- Standard HTTP 404 response
- Error message in response body

## Testing

### Manual Testing

1. **Test tag exclusion**:
   ```bash
   # Get all products
   curl http://localhost:8080/products
   
   # Exclude tag 1
   curl http://localhost:8080/products?excludeTagIds=1
   
   # Exclude multiple tags
   curl http://localhost:8080/products?excludeTagIds=1,2,3
   ```

2. **Verify tags in response**:
   - Check that each product includes a `tags` array
   - Verify tags are correctly associated

3. **Test edge cases**:
   - Empty `excludeTagIds` parameter
   - Invalid tag IDs
   - Non-existent tag IDs

## Diagrams

This documentation includes several PlantUML diagrams:

1. **[tag-filtering-architecture.puml](diagram/tag-filtering-architecture.puml)**: Overall system architecture
2. **[tag-filtering-data-flow.puml](diagram/tag-filtering-data-flow.puml)**: Sequence diagram showing data flow
3. **[database-schema.puml](diagram/database-schema.puml)**: Database schema relationships
4. **[api-endpoints.puml](diagram/api-endpoints.puml)**: API endpoint overview

To view these diagrams:
- Use a PlantUML viewer (VS Code extension, online viewer, or IDE plugin)
- Or generate images using PlantUML CLI: `plantuml diagram/*.puml`

## Future Enhancements

Potential improvements:

1. **Caching**: Cache tag lists and frequently filtered products
2. **Pagination**: Add pagination for large product lists
3. **Multiple Filter Types**: Support inclusion filtering (show only products with tags)
4. **Tag Combinations**: Support AND/OR logic for tag combinations
5. **Performance Metrics**: Add query performance monitoring

## Related Documentation

- [API Documentation](../README.md) - General API documentation
- [Database Migrations](../src/main/resources/db/migration/) - Database schema changes
- [Swagger UI](http://localhost:8080/swagger-ui) - Interactive API documentation

