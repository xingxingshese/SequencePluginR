package vanstudio.sequence.ext.kotlin;

import com.intellij.psi.PsiElement;

import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtFunction;

import vanstudio.sequence.openapi.ElementTypeFinder;

public class KtElementTypeFinder implements ElementTypeFinder {
    @Override
    public <T extends PsiElement> Class<T> findMethod() {
        return (Class<T>) KtFunction.class;
    }

    @Override
    public <T extends PsiElement> Class<T> findClass() {
        return (Class<T>) KtClass.class;
    }
}
