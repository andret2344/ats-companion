/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import eu.andret.ats.companion.idea.utilities.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class MyClassLocator {
	private MyClassLocator() {
	}

	@NotNull
	public static Collection<PsiReference> findMethod(final Project project) {
		final PsiClass psiClass = JavaPsiFacade.getInstance(project)
				.findClass(Constants.CLASS_ANNOTATED_COMMAND, GlobalSearchScope.allScope(project));
		if (psiClass == null) {
			return Collections.emptyList();
		}

		final PsiMethod[] methods = psiClass.findMethodsByName(Constants.METHOD_ADD_ARGUMENT_MAPPER, true);
		if (methods.length == 0) {
			return Collections.emptyList();
		}

		return MethodReferencesSearch.search(methods[0]).findAll();
	}
}
