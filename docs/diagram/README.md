# Tag Filtering System Diagrams

This directory contains PlantUML diagrams documenting the tag filtering system architecture and data flow.

## Diagrams

### 1. Architecture Diagram
**File**: `tag-filtering-architecture.puml`

Shows the overall system architecture including:
- Frontend components
- API layer (controllers)
- Service layer
- Repository layer
- Database relationships

**Use case**: Understanding the high-level system structure and component relationships.

### 2. Data Flow Diagram
**File**: `tag-filtering-data-flow.puml`

Sequence diagram showing:
- User interactions
- API request/response flow
- Server-side processing steps
- Database queries

**Use case**: Understanding how tag filtering works step-by-step.

### 3. Database Schema Diagram
**File**: `database-schema.puml`

Entity-relationship diagram showing:
- Database tables
- Relationships between tables
- Key fields and constraints

**Use case**: Understanding the database structure and relationships.

### 4. API Endpoints Diagram
**File**: `api-endpoints.puml`

Overview of all API endpoints organized by controller:
- Products API
- Product Tags API
- Tags API

**Use case**: Quick reference for available endpoints and their purposes.

## Viewing the Diagrams

### Option 1: VS Code Extension
1. Install the "PlantUML" extension
2. Open any `.puml` file
3. Press `Alt+D` (Windows/Linux) or `Option+D` (Mac) to preview

### Option 2: Online Viewer
1. Go to http://www.plantuml.com/plantuml/uml/
2. Copy the contents of a `.puml` file
3. Paste into the editor
4. View the rendered diagram

### Option 3: Generate Images
Using PlantUML CLI:
```bash
# Install PlantUML (requires Java)
# Download from: https://plantuml.com/starting

# Generate PNG images
plantuml diagram/*.puml

# Generate SVG images
plantuml -tsvg diagram/*.puml
```

### Option 4: IDE Plugins
- **IntelliJ IDEA**: Built-in PlantUML support
- **Eclipse**: PlantUML plugin available
- **VS Code**: PlantUML extension

## Diagram Types

### Component Diagram (Architecture)
Shows system components and their relationships.

### Sequence Diagram (Data Flow)
Shows interactions between components over time.

### Entity-Relationship Diagram (Database Schema)
Shows database tables and relationships.

### Component Diagram (API Endpoints)
Shows API organization and endpoint grouping.

## Updating Diagrams

When making changes to the system:

1. **Architecture changes**: Update `tag-filtering-architecture.puml`
2. **API changes**: Update `api-endpoints.puml`
3. **Database changes**: Update `database-schema.puml`
4. **Process changes**: Update `tag-filtering-data-flow.puml`

## Related Documentation

- [TAG_FILTERING.md](../TAG_FILTERING.md) - Complete system documentation
- [API Documentation](../../README.md) - General API documentation

