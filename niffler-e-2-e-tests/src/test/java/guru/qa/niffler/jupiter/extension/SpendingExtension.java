package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();
  private final SpendDbClient spendDbClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
            .ifPresent(
                anno -> {
                    if (ArrayUtils.isNotEmpty(anno.spendings())) {
                        Spending spendAnno = anno.spendings()[0];
                        SpendJson spendJson = new SpendJson(
                                null,
                                new Date(),
                                new CategoryJson(
                                        null,
                                        spendAnno.category(),
                                        anno.username(),
                                        false
                                ),
                                spendAnno.currency(),
                                spendAnno.amount(),
                                spendAnno.description(),
                                anno.username()
                        );
                        try {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    spendDbClient.createSpend(spendJson)
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
  }
}
