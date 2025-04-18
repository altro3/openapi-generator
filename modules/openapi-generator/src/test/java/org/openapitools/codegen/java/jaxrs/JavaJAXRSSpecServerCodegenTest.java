package org.openapitools.codegen.java.jaxrs;

import com.google.common.collect.ImmutableMap;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.openapitools.codegen.*;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.languages.AbstractJavaJAXRSServerCodegen;
import org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen;
import org.openapitools.codegen.languages.features.CXFServerFeatures;
import org.openapitools.codegen.testutils.ConfigAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.openapitools.codegen.TestUtils.*;
import static org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen.*;
import static org.openapitools.codegen.languages.features.GzipFeatures.USE_GZIP_FEATURE;
import static org.testng.Assert.assertTrue;

/**
 * Unit-Test for {@link org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen}.
 *
 * @author attrobit
 */
public class JavaJAXRSSpecServerCodegenTest extends JavaJaxrsBaseTest {

    @BeforeMethod
    public void before() {
        codegen = new JavaJAXRSSpecServerCodegen();
    }

    @Test
    public void testInitialConfigValues() throws Exception {
        final JavaJAXRSSpecServerCodegen codegen = new JavaJAXRSSpecServerCodegen();
        codegen.processOpts();

        OpenAPI openAPI = new OpenAPI();
        openAPI.addServersItem(new Server().url("https://api.abcde.xy:8082/v2"));
        codegen.preprocessOpenAPI(openAPI);

        ConfigAssert configAssert = new ConfigAssert(codegen.additionalProperties());
        configAssert.assertValue(CodegenConstants.HIDE_GENERATION_TIMESTAMP, codegen::isHideGenerationTimestamp, false);
        configAssert.assertValue(CodegenConstants.MODEL_PACKAGE, codegen::modelPackage, "org.openapitools.model");
        configAssert.assertValue(CodegenConstants.API_PACKAGE, codegen::apiPackage, "org.openapitools.api");
        configAssert.assertValue(CodegenConstants.INVOKER_PACKAGE, codegen::getInvokerPackage, "org.openapitools.api");
        codegen.additionalProperties().put(JavaJAXRSSpecServerCodegen.SERVER_PORT, "8082");
        codegen.additionalProperties().put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "src/main/openapi/openapi.yaml");
    }

    @Test
    public void testSettersForConfigValues() throws Exception {
        final JavaJAXRSSpecServerCodegen codegen = new JavaJAXRSSpecServerCodegen();
        codegen.setHideGenerationTimestamp(true);
        codegen.setModelPackage("xx.yyyyyyyy.model");
        codegen.setApiPackage("xx.yyyyyyyy.api");
        codegen.setInvokerPackage("xx.yyyyyyyy.invoker");
        codegen.setOpenApiSpecFileLocation("src/main/resources/META-INF/openapi.yaml");
        codegen.processOpts();

        ConfigAssert configAssert = new ConfigAssert(codegen.additionalProperties());
        configAssert.assertValue(CodegenConstants.HIDE_GENERATION_TIMESTAMP, codegen::isHideGenerationTimestamp, true);
        configAssert.assertValue(CodegenConstants.MODEL_PACKAGE, codegen::modelPackage, "xx.yyyyyyyy.model");
        configAssert.assertValue(CodegenConstants.API_PACKAGE, codegen::apiPackage, "xx.yyyyyyyy.api");
        configAssert.assertValue(CodegenConstants.INVOKER_PACKAGE, codegen::getInvokerPackage, "xx.yyyyyyyy.invoker");
        configAssert.assertValue(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "src/main/resources/META-INF/openapi.yaml");
    }

    @Test
    public void testAdditionalPropertiesPutForConfigValues() throws Exception {
        final JavaJAXRSSpecServerCodegen codegen = new JavaJAXRSSpecServerCodegen();
        codegen.additionalProperties().put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, "true");
        codegen.additionalProperties().put(CodegenConstants.MODEL_PACKAGE, "xyz.yyyyy.mmmmm.model");
        codegen.additionalProperties().put(CodegenConstants.API_PACKAGE, "xyz.yyyyy.aaaaa.api");
        codegen.additionalProperties().put(CodegenConstants.INVOKER_PACKAGE, "xyz.yyyyy.iiii.invoker");
        codegen.additionalProperties().put("serverPort", "8088");
        codegen.additionalProperties().put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "openapi.yml");
        codegen.additionalProperties().put(SUPPORT_ASYNC, true);
        codegen.processOpts();

        OpenAPI openAPI = new OpenAPI();
        openAPI.addServersItem(new Server().url("https://api.abcde.xy:8082/v2"));
        codegen.preprocessOpenAPI(openAPI);

        ConfigAssert configAssert = new ConfigAssert(codegen.additionalProperties());
        configAssert.assertValue(CodegenConstants.HIDE_GENERATION_TIMESTAMP, codegen::isHideGenerationTimestamp, true);
        configAssert.assertValue(CodegenConstants.MODEL_PACKAGE, codegen::modelPackage, "xyz.yyyyy.mmmmm.model");
        configAssert.assertValue(CodegenConstants.API_PACKAGE, codegen::apiPackage, "xyz.yyyyy.aaaaa.api");
        configAssert.assertValue(CodegenConstants.INVOKER_PACKAGE, codegen::getInvokerPackage, "xyz.yyyyy.iiii.invoker");
        configAssert.assertValue(AbstractJavaJAXRSServerCodegen.SERVER_PORT, "8088");
        configAssert.assertValue(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, codegen::getOpenApiSpecFileLocation, "openapi.yml");
        configAssert.assertValue(SUPPORT_ASYNC, true);
    }

    /**
     * Test
     * {@link JavaJAXRSSpecServerCodegen#addOperationToGroup(String, String, Operation, CodegenOperation, Map)} for Resource with path "/" without "useTags"
     */
    @Test
    public void testAddOperationToGroupForRootResourceAndUseTagsFalse() {
        CodegenOperation codegenOperation = new CodegenOperation();
        codegenOperation.operationId = "findPrimaryresource";
        codegenOperation.path = "/";
        Operation operation = new Operation();
        Map<String, List<CodegenOperation>> operationList = new HashMap<>();

        codegen.addOperationToGroup("Primaryresource", "/", operation, codegenOperation, operationList);

        Assert.assertEquals(operationList.size(), 1);
        Assert.assertTrue(operationList.containsKey("default"));
        Assert.assertEquals(codegenOperation.baseName, "default");
    }

    /**
     * Test
     * {@link JavaJAXRSSpecServerCodegen#addOperationToGroup(String, String, Operation, CodegenOperation, Map)} for Resource with path "/" with "useTags"
     */
    @Test
    public void testAddOperationToGroupForRootResourceAndUseTagsTrue() {
        CodegenOperation codegenOperation = new CodegenOperation();
        codegenOperation.operationId = "findPrimaryresource";
        codegenOperation.path = "/";
        Operation operation = new Operation();
        Map<String, List<CodegenOperation>> operationList = new HashMap<>();
        codegen.setUseTags(true);

        codegen.addOperationToGroup("Primaryresource", "/", operation, codegenOperation, operationList);

        Assert.assertEquals(operationList.size(), 1);
        Assert.assertTrue(operationList.containsKey("Primaryresource"));
        Assert.assertEquals(codegenOperation.baseName, "Primaryresource");
    }

    /**
     * Test
     * {@link JavaJAXRSSpecServerCodegen#addOperationToGroup(String, String, Operation, CodegenOperation, Map)} for Resource with path param.
     */
    @Test
    public void testAddOperationToGroupForRootResourcePathParamAndUseTagsFalse() {
        CodegenOperation codegenOperation = new CodegenOperation();
        codegenOperation.operationId = "getPrimaryresource";
        codegenOperation.path = "/{uuid}";
        Operation operation = new Operation();
        Map<String, List<CodegenOperation>> operationList = new HashMap<>();

        codegen.addOperationToGroup("Primaryresource", "/{uuid}", operation, codegenOperation, operationList);

        Assert.assertEquals(operationList.size(), 1);
        Assert.assertTrue(operationList.containsKey("default"));
    }

    /**
     * Test
     * {@link JavaJAXRSSpecServerCodegen#addOperationToGroup(String, String, Operation, CodegenOperation, Map)} for Resource with path param.
     */
    @Test
    public void testAddOperationToGroupForRootResourcePathParamAndUseTagsTrue() {
        CodegenOperation codegenOperation = new CodegenOperation();
        codegenOperation.operationId = "getPrimaryresource";
        codegenOperation.path = "/{uuid}";
        Operation operation = new Operation();
        Map<String, List<CodegenOperation>> operationList = new HashMap<>();
        codegen.setUseTags(true);

        codegen.addOperationToGroup("Primaryresource", "/{uuid}", operation, codegenOperation, operationList);

        Assert.assertEquals(operationList.size(), 1);
        Assert.assertTrue(operationList.containsKey("Primaryresource"));
        Assert.assertEquals(codegenOperation.baseName, "Primaryresource");
    }

    /**
     * Test
     * {@link JavaJAXRSSpecServerCodegen#addOperationToGroup(String, String,
     * Operation, CodegenOperation, Map)} for Resource with path "/subresource".
     */
    @Test
    public void testAddOperationToGroupForSubresource() {
        CodegenOperation codegenOperation = new CodegenOperation();
        codegenOperation.path = "/subresource";
        Operation operation = new Operation();
        Map<String, List<CodegenOperation>> operationList = new HashMap<>();

        codegen.addOperationToGroup("Default", "/subresource", operation, codegenOperation, operationList);

        Assert.assertEquals(codegenOperation.baseName, "subresource");
        Assert.assertEquals(operationList.size(), 1);
        assertTrue(operationList.containsKey("subresource"));
    }

    /**
     * Test {@link JavaJAXRSSpecServerCodegen#toApiName(String)} with subresource.
     */
    @Test
    public void testToApiNameForSubresource() {
        final String subresource = codegen.toApiName("subresource");
        Assert.assertEquals(subresource, "SubresourceApi");
    }

    @Test
    public void testGeneratePingDefaultLocation() throws Exception {
        Map<String, Object> properties = new HashMap<>();

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/ping.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);

        TestUtils.ensureContainsFile(files, output, "src/main/openapi/openapi.yaml");

        output.deleteOnExit();
    }

    @Test
    public void testGeneratePingDefaultArrayValue() throws Exception {
        Map<String, Object> properties = new HashMap<>();

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/ping-array-default.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);

        TestUtils.ensureContainsFile(files, output, "src/main/openapi/openapi.yaml");

        Path path = Paths.get(output.toPath() + "/src/gen/java/org/openapitools/model/AnArrayOfString.java");

        assertFileContains(path, "\nimport java.util.Arrays;\n");

        output.deleteOnExit();
    }

    @Test
    public void testGeneratePingNoSpecFile() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "");

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/ping.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);
        TestUtils.ensureDoesNotContainFile(files, output, "src/main/openapi/openapi.yaml");

        output.deleteOnExit();
    }

    @Test
    public void testGeneratePingAlternativeLocation1() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "src/main/resources/META-INF/openapi.yaml");

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/ping.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);

        TestUtils.ensureContainsFile(files, output, "src/main/resources/META-INF/openapi.yaml");

        output.deleteOnExit();
    }

    @Test
    public void testGeneratePingAlternativeLocation2() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "openapi.yml");

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/ping.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);
        TestUtils.ensureContainsFile(files, output, "openapi.yml");

        output.deleteOnExit();
    }

    @Test
    public void testGenerateApiWithPrecedingPathParameter_issue1347() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "openapi.yml");

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/issue_1347.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator(false);
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);

        TestUtils.ensureContainsFile(files, output, "openapi.yml");
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/DefaultApi.java");

        output.deleteOnExit();
    }

    @Test
    public void testGenerateApiWithCookieParameter_issue2908() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JavaJAXRSSpecServerCodegen.OPEN_API_SPEC_FILE_LOCATION, "openapi.yml");

        File output = Files.createTempDirectory("test").toFile();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName("jaxrs-spec")
                .setAdditionalProperties(properties)
                .setInputSpec("src/test/resources/3_0/issue_2908.yaml")
                .setOutputDir(output.getAbsolutePath().replace("\\", "/"));

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator(false);
        List<File> files = generator.opts(clientOptInput).generate();

        validateJavaSourceFiles(files);

        TestUtils.ensureContainsFile(files, output, "openapi.yml");
        files.stream().forEach(System.out::println);
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/SomethingApi.java");
        TestUtils.assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/SomethingApi.java"), "@CookieParam");

        output.deleteOnExit();
    }

    @Test
    public void addsImportForSetArgument() throws IOException {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();
        String outputPath = output.getAbsolutePath().replace('\\', '/');

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/setParameter.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());

        codegen.additionalProperties().put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, "true");

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator(false);
        generator.opts(input).generate();

        Path path = Paths.get(outputPath + "/src/gen/java/org/openapitools/api/ExamplesApi.java");

        assertFileContains(path, "\nimport java.util.Set;\n");
    }

    @Test
    public void addsImportForSetResponse() throws IOException {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();
        String outputPath = output.getAbsolutePath().replace('\\', '/');

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/setResponse.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());

        codegen.additionalProperties().put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, "true");

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(input).generate();

        Path path = Paths.get(outputPath + "/src/gen/java/org/openapitools/api/ExamplesApi.java");

        assertFileContains(path, "\nimport java.util.Set;\n");
    }

    @Test
    public void generateApiWithAsyncSupport() throws Exception {
        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/ping.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(SUPPORT_ASYNC, true); //Given support async is enabled

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated class contains CompletionStage<Response>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "\nimport java.util.concurrent.CompletionStage;\n",
                "\nimport java.util.concurrent.CompletableFuture;\n",
                "\npublic CompletionStage<Response> pingGet() {\n",
                "\nCompletableFuture.supplyAsync(() -> Response.ok().entity(\"magic!\").build())\n"
        );
    }

    @Test
    public void generateApiWithAsyncSupportAndInterfaceOnly() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/ping.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(SUPPORT_ASYNC, true); //Given support async is enabled
        codegen.additionalProperties().put(INTERFACE_ONLY, true); //And only interfaces are generated

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated interface contains CompletionStage<Void>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "\nimport java.util.concurrent.CompletionStage;\n",
                "\nCompletionStage<Void> pingGet();\n");
    }

    @Test
    public void generateApiWithAsyncSupportAndInterfaceOnlyAndResponse() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/ping.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(SUPPORT_ASYNC, true); //Given support async is enabled
        codegen.additionalProperties().put(INTERFACE_ONLY, true); //And only interfaces are generated
        codegen.additionalProperties().put(RETURN_RESPONSE, true); //And return type is Response

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated interface contains CompletionStage<Response>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "\nimport java.util.concurrent.CompletionStage;\n",
                "\nCompletionStage<Response> pingGet();\n");
    }


    @Test
    public void generatePetstoreAPIWithAsyncSupport() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/petstore.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(SUPPORT_ASYNC, true); //Given support async is enabled
        codegen.additionalProperties().put(INTERFACE_ONLY, true); //And only interfaces are generated

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated interfaces contains CompletionStage
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PetApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PetApi.java"),
                "\nimport java.util.concurrent.CompletionStage;\n",
                "CompletionStage<Void> deletePet", //Support empty response
                "CompletionStage<List<Pet>> findPetsByStatus", //Support type of arrays response
                "CompletionStage<Pet> getPetById" //Support single type response
        );

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/StoreApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/StoreApi.java"),
                "\nimport java.util.concurrent.CompletionStage;\n",
                "CompletionStage<Map<String, Integer>>" //Support map response
        );

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/UserApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/UserApi.java"),
                "\nimport java.util.concurrent.CompletionStage;\n",
                "CompletionStage<String>" //Support simple types
        );
    }

    @Test
    public void generatePingWithAsyncSupportPrimitiveType() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_4832.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(SUPPORT_ASYNC, true); //Given support async is enabled
        codegen.additionalProperties().put(INTERFACE_ONLY, true); //And only interfaces are generated
        codegen.additionalProperties().put(USE_TAGS, true); //And use tags to generate everything in PingApi.java

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated interfaces contains CompletionStage with proper classes instead of primitive types
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        TestUtils.assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "CompletionStage<Boolean> pingGetBoolean", //Support primitive types response
                "CompletionStage<Integer> pingGetInteger" //Support primitive types response
        );
    }

    @Test
    public void generateDeepObjectArrayWithPattern() throws IOException {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();
        String outputPath = output.getAbsolutePath().replace('\\', '/');

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/deepobject-array-with-pattern.yaml", null, new ParseOptions()).getOpenAPI();
        codegen.setOutputDir(output.getAbsolutePath());

        codegen.additionalProperties().put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, "true");

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        List<File> files = generator.opts(input).generate();

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/model/Options.java");
        TestUtils.assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/model/Options.java"), "List< @Pattern(regexp=\"^[A-Z].*\")String> getA()");
    }

    @Test
    public void testHandleDefaultValue_issue8535() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_8535.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, "true");

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        Map<String, File> files = generator.opts(input).generate().stream()
                .collect(Collectors.toMap(File::getName, Function.identity()));

        JavaFileAssert.assertThat(files.get("TestHeadersApi.java"))
                .assertMethod("headersTest")
                .assertParameter("headerNumber").hasType("BigDecimal")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"11.2\""))
                .toParameter().toMethod()
                .assertParameter("headerString").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\""))
                .toParameter().toMethod()
                .assertParameter("headerStringWrapped").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\""))
                .toParameter().toMethod()
                .assertParameter("headerStringQuotes").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\\\"with quotes\\\" test\""))
                .toParameter().toMethod()
                .assertParameter("headerStringQuotesWrapped").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\\\"with quotes\\\" test\""))
                .toParameter().toMethod()
                .assertParameter("headerBoolean").hasType("Boolean")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"true\""));

        JavaFileAssert.assertThat(files.get("TestQueryParamsApi.java"))
                .assertMethod("queryParamsTest")
                .assertParameter("queryNumber").hasType("BigDecimal")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"11.2\""))
                .toParameter().toMethod()
                .assertParameter("queryString").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\""))
                .toParameter().toMethod()
                .assertParameter("queryStringWrapped").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\""))
                .toParameter().toMethod()
                .assertParameter("queryStringQuotes").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\\\"with quotes\\\" test\""))
                .toParameter().toMethod()
                .assertParameter("queryStringQuotesWrapped").hasType("String")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"qwerty\\\"with quotes\\\" test\""))
                .toParameter().toMethod()
                .assertParameter("queryBoolean").hasType("Boolean")
                .assertParameterAnnotations()
                .containsWithNameAndAttributes("DefaultValue", ImmutableMap.of("value", "\"true\""));
    }

    @Test
    public void testValidAnnotation_issue14432() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_14432.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        Map<String, File> files = generator.opts(input).generate().stream()
                .collect(Collectors.toMap(File::getName, Function.identity()));

        JavaFileAssert.assertThat(files.get("ComplexObject.java"))
                .fileContains("private @Valid List<LocalDate> dates")
                .fileDoesNotContain("private @Valid SymbolTypeEnum symbolType")
                .fileDoesNotContain("@Valid String")
                .fileDoesNotContain("@Valid Double");
    }

    @Test
    public void arrayNullableDefaultValueTests() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_13025.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(SUPPORT_ASYNC, true); //Given support async is enabled
        codegen.additionalProperties().put(INTERFACE_ONLY, true); //And only interfaces are generated

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated model contains correct default value for array properties (optional)
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/model/Body.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/model/Body.java"),
                "\nprivate @Valid List<String> arrayThatIsNull;\n");

        //And the generated model contains correct default value for array properties (required, nullable)
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/model/BodyWithRequiredNullable.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/model/BodyWithRequiredNullable.java"),
                "\nprivate @Valid List<String> arrayThatIsNull;\n");

        //And the generated model contains correct default value for array properties (required)
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/model/BodyWithRequired.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/model/BodyWithRequired.java"),
                "\nprivate @Valid List<String> arrayThatIsNotNull = new ArrayList<>();\n");

    }

    @Test
    public void generateApiForQuarkusWithGzipFeature() throws Exception {
        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/ping.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.setLibrary(QUARKUS_LIBRARY);
        codegen.additionalProperties().put(USE_GZIP_FEATURE, true);

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated class contains CompletionStage<Response>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "\nimport org.jboss.resteasy.annotations.GZIP\n",
                "@GZIP\n"
        );
    }

    @Test
    public void generateApiForQuarkusWithoutMutiny() throws Exception {
        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_4832.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.setLibrary(QUARKUS_LIBRARY);
        codegen.additionalProperties().put(SUPPORT_ASYNC, true);
        codegen.additionalProperties().put(USE_TAGS, true); //And use tags to generate everything in PingApi.java

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated class contains CompletionStage<Response>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        TestUtils.assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "CompletionStage<Response> pingGetBoolean", //Support primitive types response
                "CompletionStage<Response> pingGetInteger" //Support primitive types response
        );
    }

    @Test
    public void generateApiForQuarkusWithMutinyApi() throws Exception {
        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_4832.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.setLibrary(QUARKUS_LIBRARY);
        codegen.additionalProperties().put(USE_TAGS, true); //And use tags to generate everything in PingApi.java
        codegen.additionalProperties().put(SUPPORT_ASYNC, true);
        codegen.additionalProperties().put(INTERFACE_ONLY, true);
        codegen.additionalProperties().put(USE_MUTINY, true);

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated class contains CompletionStage<Response>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        TestUtils.assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "Uni<Boolean> pingGetBoolean", //Support primitive types response
                "Uni<Integer> pingGetInteger" //Support primitive types response
        );
    }


    @Test
    public void generateApiForQuarkusWithMutinyImpl() throws Exception {
        final File output = Files.createTempDirectory("test").toFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/issue_4832.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.setLibrary(QUARKUS_LIBRARY);
        codegen.additionalProperties().put(USE_TAGS, true); //And use tags to generate everything in PingApi.java
        codegen.additionalProperties().put(SUPPORT_ASYNC, true);
        codegen.additionalProperties().put(USE_MUTINY, true);

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //Using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated class contains CompletionStage<Response>
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PingApi.java");
        TestUtils.assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PingApi.java"),
                "Uni<Response> pingGetBoolean", //Support primitive types response
                "Uni<Response> pingGetInteger" //Support primitive types response
        );
    }

    @Test
    public void testHandleRequiredAndReadOnlyPropertiesCorrectly() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/required-and-readonly-property.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        Map<String, File> files = generator.opts(input).generate().stream()
                .collect(Collectors.toMap(File::getName, Function.identity()));

        JavaFileAssert.assertThat(files.get("ReadonlyAndRequiredProperties.java"))
                .assertProperty("requiredYesReadonlyYes")
                .toType()
                .assertMethod("getRequiredYesReadonlyYes")
                .assertMethodAnnotations()
                .hasSize(2)
                .containsWithNameAndAttributes("ApiModelProperty", ImmutableMap.of("required", "true"))
                // Mysteriously, but we need to surround the value with quotes if the Annotation only contains a single value
                .containsWithNameAndAttributes("JsonProperty", ImmutableMap.of("value", "\"requiredYesReadonlyYes\""))
                .toMethod()
                .toFileAssert()
                .assertProperty("requiredYesReadonlyNo")
                .toType()
                .assertMethod("getRequiredYesReadonlyNo")
                .assertMethodAnnotations()
                .hasSize(3)
                .containsWithNameAndAttributes("ApiModelProperty", ImmutableMap.of("required", "true"))
                // Mysteriously, but we need to surround the value with quotes if the Annotation only contains a single value
                .containsWithNameAndAttributes("JsonProperty", ImmutableMap.of("value", "\"requiredYesReadonlyNo\""))
                .containsWithName("NotNull");
    }

    @Test
    public void generateSpecInterfaceWithMicroprofileOpenApiAnnotations() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/petstore.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(INTERFACE_ONLY, true); //And only interfaces are generated
        codegen.additionalProperties().put(USE_MICROPROFILE_OPENAPI_ANNOTATIONS, true); //And only interfaces are generated
        codegen.additionalProperties().put(USE_TAGS, true); //And use tags to generate everything in several API files
        codegen.additionalProperties().put(RETURN_RESPONSE, true); // Retrieve HTTP Response
        codegen.additionalProperties().put(USE_JAKARTA_EE, true); // Use Jakarta
        codegen.setLibrary(QUARKUS_LIBRARY); // Set Quarkus

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated interfaces contains CompletionStage
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PetApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PetApi.java"),
                "@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(\n" +
                        "   info = @org.eclipse.microprofile.openapi.annotations.info.Info(\n" +
                        "        title = \"pet\", version=\"1.0.0\", description=\"Everything about your Pets\",");

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/StoreApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/StoreApi.java"),
                "@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(\n" +
                        "   info = @org.eclipse.microprofile.openapi.annotations.info.Info(\n" +
                        "        title = \"store\", version=\"1.0.0\", description=\"Access to Petstore orders\",");

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/UserApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/UserApi.java"),
                "@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(\n" +
                        "   info = @org.eclipse.microprofile.openapi.annotations.info.Info(\n" +
                        "        title = \"user\", version=\"1.0.0\", description=\"Operations about user\",");
    }

    @Test
    public void generateSpecNonInterfaceWithMicroprofileOpenApiAnnotations() throws Exception {
        final File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        final OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/petstore.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(INTERFACE_ONLY, false); //And only interfaces are generated
        codegen.additionalProperties().put(USE_MICROPROFILE_OPENAPI_ANNOTATIONS, true); //And only interfaces are generated
        codegen.additionalProperties().put(USE_TAGS, true); //And use tags to generate everything in several API files
        codegen.additionalProperties().put(RETURN_RESPONSE, true); // Retrieve HTTP Response
        codegen.additionalProperties().put(USE_JAKARTA_EE, true); // Use Jakarta
        codegen.setLibrary(QUARKUS_LIBRARY); // Set Quarkus

        final ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen); //using JavaJAXRSSpecServerCodegen

        final DefaultGenerator generator = new DefaultGenerator();
        final List<File> files = generator.opts(input).generate(); //When generating files

        //Then the java files are compilable
        validateJavaSourceFiles(files);

        //And the generated interfaces contains CompletionStage
        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/PetApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/PetApi.java"),
                "@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(\n" +
                        "   info = @org.eclipse.microprofile.openapi.annotations.info.Info(\n" +
                        "        title = \"pet\", version=\"1.0.0\", description=\"Everything about your Pets\",");

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/StoreApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/StoreApi.java"),
                "@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(\n" +
                        "   info = @org.eclipse.microprofile.openapi.annotations.info.Info(\n" +
                        "        title = \"store\", version=\"1.0.0\", description=\"Access to Petstore orders\",");

        TestUtils.ensureContainsFile(files, output, "src/gen/java/org/openapitools/api/UserApi.java");
        assertFileContains(output.toPath().resolve("src/gen/java/org/openapitools/api/UserApi.java"),
                "@org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition(\n" +
                        "   info = @org.eclipse.microprofile.openapi.annotations.info.Info(\n" +
                        "        title = \"user\", version=\"1.0.0\", description=\"Operations about user\",");
    }

    @Test
    public void testEnumUnknownDefaultCaseDeserializationTrue_issue13444() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/bugs/issue_13444.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        codegen.additionalProperties().put(CodegenConstants.ENUM_UNKNOWN_DEFAULT_CASE, "true");

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        Map<String, File> files = generator.opts(input).generate().stream()
                .collect(Collectors.toMap(File::getName, Function.identity()));

        JavaFileAssert.assertThat(files.get("Color.java"))
                .assertMethod("fromValue").bodyContainsLines("return UNKNOWN_DEFAULT_OPEN_API");

    }

    @Test
    public void testEnumUnknownDefaultCaseDeserializationNotSet_issue13444() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/bugs/issue_13444.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        Map<String, File> files = generator.opts(input).generate().stream()
                .collect(Collectors.toMap(File::getName, Function.identity()));

        JavaFileAssert.assertThat(files.get("Color.java"))
                .assertMethod("fromValue").bodyContainsLines("throw new IllegalArgumentException(\"Unexpected value '\" + value + \"'\");");

    }

    @Test
    public void disableGenerateJsonCreator() throws Exception {
        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        output.deleteOnExit();

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation("src/test/resources/3_0/required-properties.yaml", null, new ParseOptions()).getOpenAPI();

        codegen.setOutputDir(output.getAbsolutePath());
        ((JavaJAXRSSpecServerCodegen) codegen).setGenerateJsonCreator(false);

        ClientOptInput input = new ClientOptInput()
                .openAPI(openAPI)
                .config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        Map<String, File> files = generator.opts(input).generate().stream()
                .collect(Collectors.toMap(File::getName, Function.identity()));

        assertFileNotContains(files.get("RequiredProperties.java").toPath(), "@JsonCreator");
    }

}
