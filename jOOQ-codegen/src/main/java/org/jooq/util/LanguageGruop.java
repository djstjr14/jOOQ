/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 */
package org.jooq.util;


import static org.jooq.tools.StringUtils.isBlank;
import static org.jooq.util.AbstractGenerator.Language.JAVA;
import static org.jooq.util.AbstractGenerator.Language.SCALA;
import static org.jooq.util.AbstractGenerator.Language.XML;
import static org.jooq.util.xml.jaxb.TableConstraintType.CHECK;
import static org.jooq.util.xml.jaxb.TableConstraintType.FOREIGN_KEY;
import static org.jooq.util.xml.jaxb.TableConstraintType.PRIMARY_KEY;
import static org.jooq.util.xml.jaxb.TableConstraintType.UNIQUE;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXB;

import org.jooq.SortOrder;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StopWatch;
import org.jooq.tools.StringUtils;
//import org.jooq.util.LanguageGruop.AvoidAmbiguousClassesFilter;
import org.jooq.util.xml.jaxb.Catalog;
import org.jooq.util.xml.jaxb.Column;
import org.jooq.util.xml.jaxb.Index;
import org.jooq.util.xml.jaxb.IndexColumnUsage;
import org.jooq.util.xml.jaxb.InformationSchema;
import org.jooq.util.xml.jaxb.KeyColumnUsage;
import org.jooq.util.xml.jaxb.Parameter;
import org.jooq.util.xml.jaxb.ParameterMode;
import org.jooq.util.xml.jaxb.ReferentialConstraint;
import org.jooq.util.xml.jaxb.Routine;
import org.jooq.util.xml.jaxb.RoutineType;
import org.jooq.util.xml.jaxb.Schema;
import org.jooq.util.xml.jaxb.Sequence;
import org.jooq.util.xml.jaxb.Table;
import org.jooq.util.xml.jaxb.TableConstraint;


/**
 * A default implementation for code generation.
 * <p>
 * Replace this code with your own logic, if you need your database schema
 * represented in a different way.
 * <p>
 * Note that you can also extend this class to generate POJO's or other stuff
 * entirely independent of jOOQ.
 *
 * @author Lukas Eder
 */
public class LanguageGruop extends AbstractGenerator {
	
    private static final JooqLogger               log                          = JooqLogger.getLogger(JavaGenerator.class);

    /**
     * The Javadoc to be used for private constructors
     */
    private static final String                   NO_FURTHER_INSTANCES_ALLOWED = "No further instances allowed";

    /**
     * [#1459] Prevent large static initialisers by splitting nested classes
     */
    private static final int                      INITIALISER_SIZE             = 500;

    /**
     * [#4429] A map providing access to SQLDataType member literals
     */
//    private static final Map<DataType<?>, String> SQLDATATYPE_LITERAL_LOOKUP;
//
//    /**
//     * [#6411] A set providing access to SQLDataTypes that can have length.
//     */
//    private static final Set<String>              SQLDATATYPE_WITH_LENGTH;
//
//    /**
//     * [#6411] A set providing access to SQLDataTypes that can have precision
//     * (and scale).
//     */
//    private static final Set<String>              SQLDATATYPE_WITH_PRECISION;
//
//    /**
//     * An overall stop watch to measure the speed of source code generation
//     */
    private final StopWatch                       watch                        = new StopWatch();

    /**
     * The underlying database of this generator
     */
    private Database                              database;

    /**
     * The code generation date, if needed.
     */
    private String                                isoDate;

    /**
     * The cached schema version numbers.
     */
    private Map<SchemaDefinition, String>         schemaVersions;

    /**
     * The cached catalog version numbers.
     */
    private Map<CatalogDefinition, String>        catalogVersions;

    /**
     * All files modified by this generator.
     */
    private Set<File>                             files                        = new LinkedHashSet<File>();

    /**
     * These directories were not modified by this generator, but flagged as not
     * for removal (e.g. because of {@link #schemaVersions} or
     * {@link #catalogVersions}).
     */
    private Set<File>                             directoriesNotForRemoval     = new LinkedHashSet<File>();

    private final boolean                         scala;
    private final String                          tokenVoid;
    
    
	
    LanguageGruop(Language language) { //JAVA or SCALA or XML
        super(language);

        this.scala = (language == SCALA);
        this.tokenVoid = (scala ? "Unit" : "void");
    }
    
    private List<AbstractGenerator> LanguageGenerators = new ArrayList<AbstractGenerator>();
    
    public void addLanguageGenerator(AbstractGenerator LanguageGenerator) { LanguageGenerators.add(LanguageGenerator); }
	
    public final void generate(Database db) {
    	for(int num = 0 ; num<LanguageGenerators.size(); num++ ) { //XML OR JAVA OR SCALA (LANGUAGE)
			if(language == LanguageGenerators.get(num).language.XML) {
				logDatabaseParameters(db);
			    log.info("");
			    logGenerationRemarks(db);
			
			    log.info("");
			    log.info("----------------------------------------------------------");
			
			    TextWriter out = new TextWriter(getStrategy().getFile("information_schema.xml"), targetEncoding);
			    log.info("");
			    log.info("Generating XML", out.file().getName());
			    log.info("==========================================================");
			
			    InformationSchema is = new InformationSchema();
			
			    boolean hasNonDefaultCatalogs = false;
			    for (CatalogDefinition c : db.getCatalogs()) {
			        if (!StringUtils.isBlank(c.getName())) {
			            hasNonDefaultCatalogs = true;
			            break;
			        }
			    }
			
			    for (CatalogDefinition c : db.getCatalogs()) {
			        String catalogName = c.getOutputName();
			
			        if (hasNonDefaultCatalogs)
			            is.getCatalogs().add(new Catalog()
			                .withCatalogName(catalogName)
			                .withComment(generateCommentsOnCatalogs() ? c.getComment() : null));
			
			        for (SchemaDefinition s : c.getSchemata()) {
			            String schemaName = s.getOutputName();
			
			            Schema schema = new Schema();
			            schema.setCatalogName(catalogName);
			            schema.setSchemaName(schemaName);
			
			            if (generateCommentsOnSchemas())
			                schema.setComment(s.getComment());
			
			            is.getSchemata().add(schema);
			
			            for (TableDefinition t : s.getTables()) {
			                String tableName = t.getOutputName();
			
			                Table table = new Table();
			                table.setTableCatalog(catalogName);
			                table.setTableSchema(schemaName);
			                table.setTableName(tableName);
			
			                if (generateCommentsOnTables())
			                    table.setComment(t.getComment());
			
			                is.getTables().add(table);
			
			                for (ColumnDefinition co : t.getColumns()) {
			                    String columnName = co.getOutputName();
			                    DataTypeDefinition type = co.getType();
			
			                    Column column = new Column();
			                    column.setTableCatalog(catalogName);
			                    column.setTableSchema(schemaName);
			                    column.setTableName(tableName);
			                    column.setColumnName(columnName);
			
			                    if (generateCommentsOnColumns())
			                        column.setComment(co.getComment());
			
			                    column.setCharacterMaximumLength(type.getLength());
			                    column.setColumnDefault(type.getDefaultValue());
			                    column.setDataType(type.getType());
			                    if (co.isIdentity())
			                        column.setIdentityGeneration("YES");
			                    column.setIsNullable(type.isNullable());
			                    column.setNumericPrecision(type.getPrecision());
			                    column.setNumericScale(type.getScale());
			                    column.setOrdinalPosition(co.getPosition());
			
			                    is.getColumns().add(column);
			                }
			            }
			
			            for (IndexDefinition i : db.getIndexes(s)) {
			                String indexName = i.getOutputName();
			                TableDefinition table = i.getTable();
			                List<IndexColumnDefinition> columns = i.getIndexColumns();
			
			                Index index = new Index();
			                index.setIndexCatalog(catalogName);
			                index.setIndexSchema(schemaName);
			                index.setIndexName(indexName);
			
			                if (generateCommentsOnKeys())
			                    index.setComment(i.getComment());
			
			                index.setTableCatalog(table.getCatalog().getOutputName());
			                index.setTableSchema(table.getSchema().getOutputName());
			                index.setTableName(table.getOutputName());
			                index.setIsUnique(i.isUnique());
			
			                is.getIndexes().add(index);
			
			                for (int j = 0; j < columns.size(); j++) {
			                    IndexColumnDefinition indexColumn = columns.get(j);
			                    ColumnDefinition column = indexColumn.getColumn();
			
			                    IndexColumnUsage ic = new IndexColumnUsage();
			                    ic.setIndexCatalog(catalogName);
			                    ic.setIndexSchema(schemaName);
			                    ic.setIndexName(indexName);
			                    ic.setColumnName(column.getOutputName());
			                    ic.setOrdinalPosition(j + 1);
			                    ic.setIsDescending(indexColumn.getSortOrder() == SortOrder.DESC);
			                    ic.setTableCatalog(table.getCatalog().getOutputName());
			                    ic.setTableSchema(table.getSchema().getOutputName());
			                    ic.setTableName(table.getOutputName());
			
			                    is.getIndexColumnUsages().add(ic);
			                }
			            }
			
			            for (UniqueKeyDefinition u : db.getUniqueKeys(s)) {
			                String constraintName = u.getOutputName();
			                TableDefinition table = u.getTable();
			                List<ColumnDefinition> columns = u.getKeyColumns();
			
			                TableConstraint constraint = new TableConstraint();
			                constraint.setConstraintCatalog(catalogName);
			                constraint.setConstraintSchema(schemaName);
			                constraint.setConstraintName(constraintName);
			                constraint.setConstraintType(u.isPrimaryKey() ? PRIMARY_KEY : UNIQUE);
			
			                if (generateCommentsOnKeys())
			                    constraint.setComment(u.getComment());
			
			                constraint.setTableCatalog(table.getCatalog().getOutputName());
			                constraint.setTableSchema(table.getSchema().getOutputName());
			                constraint.setTableName(table.getOutputName());
			
			                is.getTableConstraints().add(constraint);
			
			                for (int i = 0; i < columns.size(); i++) {
			                    ColumnDefinition column = columns.get(i);
			
			                    KeyColumnUsage kc = new KeyColumnUsage();
			
			                    kc.setConstraintCatalog(catalogName);
			                    kc.setConstraintSchema(schemaName);
			                    kc.setConstraintName(constraintName);
			                    kc.setColumnName(column.getOutputName());
			                    kc.setOrdinalPosition(i);
			                    kc.setTableCatalog(table.getCatalog().getOutputName());
			                    kc.setTableSchema(table.getSchema().getOutputName());
			                    kc.setTableName(table.getOutputName());
			
			                    is.getKeyColumnUsages().add(kc);
			                }
			            }
			
			            for (ForeignKeyDefinition f : db.getForeignKeys(s)) {
			                String constraintName = f.getOutputName();
			                UniqueKeyDefinition referenced = f.getReferencedKey();
			                TableDefinition table = f.getKeyTable();
			                List<ColumnDefinition> columns = f.getKeyColumns();
			
			                TableConstraint tc = new TableConstraint();
			                tc.setConstraintCatalog(catalogName);
			                tc.setConstraintSchema(schemaName);
			                tc.setConstraintName(constraintName);
			                tc.setConstraintType(FOREIGN_KEY);
			
			                if (generateCommentsOnKeys())
			                    tc.setComment(f.getComment());
			
			                tc.setTableCatalog(table.getCatalog().getOutputName());
			                tc.setTableSchema(table.getSchema().getOutputName());
			                tc.setTableName(table.getOutputName());
			
			                ReferentialConstraint rc = new ReferentialConstraint();
			                rc.setConstraintCatalog(catalogName);
			                rc.setConstraintSchema(schemaName);
			                rc.setConstraintName(constraintName);
			                rc.setUniqueConstraintCatalog(referenced.getCatalog().getOutputName());
			                rc.setUniqueConstraintSchema(referenced.getSchema().getOutputName());
			                rc.setUniqueConstraintName(referenced.getOutputName());
			
			                is.getTableConstraints().add(tc);
			                is.getReferentialConstraints().add(rc);
			
			                for (int i = 0; i < columns.size(); i++) {
			                    ColumnDefinition column = columns.get(i);
			
			                    KeyColumnUsage kc = new KeyColumnUsage();
			
			                    kc.setConstraintCatalog(catalogName);
			                    kc.setConstraintSchema(schemaName);
			                    kc.setConstraintName(constraintName);
			                    kc.setColumnName(column.getOutputName());
			                    kc.setOrdinalPosition(i);
			                    kc.setTableCatalog(table.getCatalog().getOutputName());
			                    kc.setTableSchema(table.getSchema().getOutputName());
			                    kc.setTableName(table.getOutputName());
			
			                    is.getKeyColumnUsages().add(kc);
			                }
			            }
			
			            for (CheckConstraintDefinition ch : db.getCheckConstraints(s)) {
			                String constraintName = ch.getOutputName();
			                TableDefinition table = ch.getTable();
			
			                TableConstraint constraint = new TableConstraint();
			                constraint.setConstraintCatalog(catalogName);
			                constraint.setConstraintSchema(schemaName);
			                constraint.setConstraintName(constraintName);
			                constraint.setConstraintType(CHECK);
			
			                if (generateCommentsOnKeys())
			                    constraint.setComment(ch.getComment());
			
			                constraint.setTableCatalog(table.getCatalog().getOutputName());
			                constraint.setTableSchema(table.getSchema().getOutputName());
			                constraint.setTableName(table.getOutputName());
			
			                is.getTableConstraints().add(constraint);
			            }
			
			            for (SequenceDefinition se : db.getSequences(s)) {
			                String sequenceName = se.getOutputName();
			                DataTypeDefinition type = se.getType();
			
			                Sequence sequence = new Sequence();
			                sequence.setSequenceCatalog(catalogName);
			                sequence.setSequenceSchema(schemaName);
			                sequence.setSequenceName(sequenceName);
			
			                if (generateCommentsOnSequences())
			                    sequence.setComment(se.getComment());
			
			                sequence.setCharacterMaximumLength(type.getLength());
			                sequence.setDataType(type.getType());
			                sequence.setNumericPrecision(type.getPrecision());
			                sequence.setNumericScale(type.getScale());
			
			                is.getSequences().add(sequence);
			            }
			
			            for (PackageDefinition pkg : db.getPackages(s))
			                for (RoutineDefinition r : pkg.getRoutines())
			                    exportRoutine(is, r, catalogName, schemaName);
			
			            for (RoutineDefinition r : db.getRoutines(s))
			                exportRoutine(is, r, catalogName, schemaName);
			        }
			    }
			
			    StringWriter writer = new StringWriter();
			    JAXB.marshal(is, writer);
			    out.print(writer.toString());
			    out.close();
			}
			else if(language == LanguageGenerators.get(num).language.JAVA || language == LanguageGenerators.get(num).language.SCALA) {
				
				this.isoDate = DatatypeConverter.printDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
			    this.schemaVersions = new LinkedHashMap<SchemaDefinition, String>();
			    this.catalogVersions = new LinkedHashMap<CatalogDefinition, String>();
			
			    this.database = db;
			    this.database.addFilter(new AvoidAmbiguousClassesFilter());
			    this.database.setIncludeRelations(generateRelations());
			    this.database.setTableValuedFunctions(generateTableValuedFunctions());
			
			    logDatabaseParameters(db);
			    log.info("");
			    log.info("JavaGenerator parameters");
			    log.info("----------------------------------------------------------");
			    log.info("  annotations (generated)", generateGeneratedAnnotation()
			        + ((!generateGeneratedAnnotation && (useSchemaVersionProvider || useCatalogVersionProvider)) ?
			            " (forced to true because of <schemaVersionProvider/> or <catalogVersionProvider/>)" : ""));
			    log.info("  annotations (JPA: any)", generateJPAAnnotations());
			    log.info("  annotations (JPA: version)", generateJPAVersion());
			    log.info("  annotations (validation)", generateValidationAnnotations());
			    log.info("  comments", generateComments());
			    log.info("  comments on attributes", generateCommentsOnAttributes());
			    log.info("  comments on catalogs", generateCommentsOnCatalogs());
			    log.info("  comments on columns", generateCommentsOnColumns());
			    log.info("  comments on keys", generateCommentsOnKeys());
			    log.info("  comments on links", generateCommentsOnLinks());
			    log.info("  comments on packages", generateCommentsOnPackages());
			    log.info("  comments on parameters", generateCommentsOnParameters());
			    log.info("  comments on queues", generateCommentsOnQueues());
			    log.info("  comments on routines", generateCommentsOnRoutines());
			    log.info("  comments on schemas", generateCommentsOnSchemas());
			    log.info("  comments on sequences", generateCommentsOnSequences());
			    log.info("  comments on tables", generateCommentsOnTables());
			    log.info("  comments on udts", generateCommentsOnUDTs());
			    log.info("  daos", generateDaos());
			    log.info("  deprecated code", generateDeprecated());
			    log.info("  global references (any)", generateGlobalObjectReferences());
			    log.info("  global references (catalogs)", generateGlobalCatalogReferences());
			    log.info("  global references (keys)", generateGlobalKeyReferences());
			    log.info("  global references (links)", generateGlobalLinkReferences());
			    log.info("  global references (queues)", generateGlobalQueueReferences());
			    log.info("  global references (routines)", generateGlobalRoutineReferences());
			    log.info("  global references (schemas)", generateGlobalSchemaReferences());
			    log.info("  global references (sequences)", generateGlobalSequenceReferences());
			    log.info("  global references (tables)", generateGlobalTableReferences());
			    log.info("  global references (udts)", generateGlobalUDTReferences());
			    log.info("  indexes", generateIndexes());
			    log.info("  instance fields", generateInstanceFields());
			    log.info("  interfaces", generateInterfaces()
			          + ((!generateInterfaces && generateImmutableInterfaces) ? " (forced to true because of <immutableInterfaces/>)" : ""));
			    log.info("  interfaces (immutable)", generateInterfaces());
			    log.info("  javadoc", generateJavadoc());
			    log.info("  keys", generateKeys());
			    log.info("  links", generateLinks());
			    log.info("  pojos", generatePojos()
			          + ((!generatePojos && generateDaos) ? " (forced to true because of <daos/>)" :
			            ((!generatePojos && generateImmutablePojos) ? " (forced to true because of <immutablePojos/>)" : "")));
			    log.info("  pojos (immutable)", generateImmutablePojos());
			    log.info("  queues", generateQueues());
			    log.info("  records", generateRecords()
			          + ((!generateRecords && generateDaos) ? " (forced to true because of <daos/>)" : ""));
			    log.info("  routines", generateRoutines());
			    log.info("  sequences", generateSequences());
			    log.info("  table-valued functions", generateTableValuedFunctions());
			    log.info("  tables", generateTables()
			          + ((!generateTables && generateRecords) ? " (forced to true because of <records/>)" :
			            ((!generateTables && generateDaos) ? " (forced to true because of <daos/>)" : "")));
			    log.info("  udts", generateUDTs());
			    log.info("  relations", generateRelations()
			        + ((!generateRelations && generateTables) ? " (forced to true because of <tables/>)" :
			          ((!generateRelations && generateDaos) ? " (forced to true because of <daos/>)" : "")));
			    log.info("----------------------------------------------------------");
			
			    if (!generateInstanceFields()) {
			        log.warn("");
			        log.warn("Deprecation warnings");
			        log.warn("----------------------------------------------------------");
			        log.warn("  <generateInstanceFields/> = false is deprecated! Please adapt your configuration.");
			    }
			
			    log.info("");
			    logGenerationRemarks(db);
			
			    log.info("");
			    log.info("----------------------------------------------------------");
			
			    // ----------------------------------------------------------------------
			    // XXX Generating catalogs
			    // ----------------------------------------------------------------------
			    log.info("Generating catalogs", "Total: " + database.getCatalogs().size());
			    for (CatalogDefinition catalog : database.getCatalogs()) {
			        try {
			            if (generateCatalogIfEmpty(catalog))
			            	generate((Database) catalog);
			            else
			                log.info("Excluding empty catalog", catalog);
			        }
			        catch (Exception e) {
			            throw new GeneratorException("Error generating code for catalog " + catalog, e);
			        }
			    }
			
			    // [#5556] Clean up common parent directory
			    log.info("Removing excess files");
			    empty(getStrategy().getFileRoot(), (scala ? ".scala" : ".java"), files, directoriesNotForRemoval);
			    directoriesNotForRemoval.clear();
			    files.clear();
			}
    	}
	}
	
	private void exportRoutine(InformationSchema is, RoutineDefinition r, String catalogName, String schemaName) {
        String specificName = r.getName() + (isBlank(r.getOverload()) ? "" : "_" + r.getOverload());

        Routine routine = new Routine();
        routine.setRoutineCatalog(catalogName);
        routine.setSpecificCatalog(catalogName);
        routine.setRoutineSchema(schemaName);
        routine.setSpecificSchema(schemaName);

        if (r.getPackage() != null) {
            routine.setRoutinePackage(r.getPackage().getName());
            routine.setSpecificPackage(r.getPackage().getName());
        }

        routine.setRoutineName(r.getName());
        routine.setSpecificName(specificName);

        if (generateCommentsOnRoutines())
            routine.setComment(r.getComment());

        if (r.getReturnValue() == null) {
            routine.setRoutineType(RoutineType.PROCEDURE);
        }
        else {
            routine.setRoutineType(RoutineType.FUNCTION);
            routine.setDataType(r.getReturnType().getType());
            routine.setCharacterMaximumLength(r.getReturnType().getLength());
            routine.setNumericPrecision(r.getReturnType().getPrecision());
            routine.setNumericScale(r.getReturnType().getScale());
        }

        is.getRoutines().add(routine);

        int i = 1;
        for (ParameterDefinition p : r.getAllParameters()) {
            if (p != r.getReturnValue()) {
                Parameter parameter = new Parameter();

                parameter.setSpecificCatalog(catalogName);
                parameter.setSpecificSchema(schemaName);

                if (r.getPackage() != null)
                    parameter.setSpecificPackage(r.getPackage().getName());

                parameter.setSpecificName(specificName);
                parameter.setOrdinalPosition(i++);
                parameter.setParameterName(p.getName());

                if (generateCommentsOnParameters())
                    parameter.setComment(p.getComment());

                boolean in = r.getInParameters().contains(p);
                boolean out = r.getOutParameters().contains(p);

                if (in && out)
                    parameter.setParameterMode(ParameterMode.INOUT);
                else if (in)
                    parameter.setParameterMode(ParameterMode.IN);
                else if (out)
                    parameter.setParameterMode(ParameterMode.OUT);

                parameter.setDataType(p.getType().getType());
                parameter.setCharacterMaximumLength(p.getType().getLength());
                parameter.setNumericPrecision(p.getType().getPrecision());
                parameter.setNumericScale(p.getType().getScale());
                parameter.setParameterDefault(p.getType().getDefaultValue());

                is.getParameters().add(parameter);
            }
        }
    }
	private class AvoidAmbiguousClassesFilter implements Database.Filter {

        private Map<String, String> included = new HashMap<String, String>();

        @Override
        public boolean exclude(Definition definition) {

            // These definitions don't generate types of their own.
            if (    definition instanceof ColumnDefinition
                 || definition instanceof AttributeDefinition
                 || definition instanceof ParameterDefinition)
                return false;

            // Check if we've previously encountered a Java type of the same case-insensitive, fully-qualified name.
            String name = getStrategy().getFullJavaClassName(definition);
            String nameLC = name.toLowerCase();
            String existing = included.put(nameLC, name);

            if (existing == null)
                return false;

            log.warn("Ambiguous type name", "The object " + definition.getQualifiedOutputName() + " generates a type " + name + " which conflicts with the existing type " + existing + " on some operating systems. Use a custom generator strategy to disambiguate the types.");
            return true;
        }
    }
	private boolean generateCatalogIfEmpty(CatalogDefinition catalog) {
        if (generateEmptyCatalogs())
            return true;

        List<SchemaDefinition> schemas = catalog.getSchemata();
        if (schemas.isEmpty())
            return false;

        for (SchemaDefinition schema : schemas)
            if (generateSchemaIfEmpty(schema))
                return true;

        return false;
    }
	 private final boolean generateSchemaIfEmpty(SchemaDefinition schema) {
	        if (generateEmptySchemas())
	            return true;
	
	        if (database.getArrays(schema).isEmpty()
	            && database.getEnums(schema).isEmpty()
	            && database.getPackages(schema).isEmpty()
	            && database.getRoutines(schema).isEmpty()
	            && database.getTables(schema).isEmpty()
	            && database.getUDTs(schema).isEmpty())
	            return false;
	
	        return true;
	    }

}
