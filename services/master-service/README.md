# master-service
Reference data service. Owns all lookup/master tables: items, UOM, locations, companies, call types, terms.

## Port: 8083
## Database: vsms_master

## Responsibility
- Item and product catalog (mst_item)
- Unit of measure (mst_uom)
- Category and sub-category (mst_category, mst_subcategory)
- Location and branch management (mst_location)
- Company master (mst_company)
- Geographic reference data (mst_state, mst_country)
- Terms, call types, signatures, industries

## Tables Owned
mst_company, mst_item, mst_uom, mst_category, mst_subcategory, mst_location,
mst_state, mst_country, mst_currency, mst_consignee, mst_term, mst_industry,
mst_signature, mst_call_type

## Events Published
None — master-service is read-heavy, no domain events

## Events Consumed
None

## Feign Clients Used
None — master-service has no upstream service dependencies

## Developer TODO
- [ ] Add Item, Company, Location, Uom, Category entities with all fields
- [ ] Implement ItemService, CompanyService, LocationService, UomService
- [ ] Add caching (@Cacheable) on read methods — master data rarely changes
- [ ] Migrate Flyway DDL from monolith V6, V11, V33+