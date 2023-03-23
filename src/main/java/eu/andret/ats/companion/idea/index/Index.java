/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.index;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex.InputFilter;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.PsiDependentFileContent;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Index extends FileBasedIndexExtension<String, String> {
	@NonNls
	public static final ID<String, String> NAME = ID.create("eu.andret.ats.companion.idea.Index");

	@NotNull
	@Override
	public ID<String, String> getName() {
		return NAME;
	}

	@NotNull
	@Override
	public DataIndexer<String, String, FileContent> getIndexer() {
		return new MyDataIndexer();
	}

	@NotNull
	@Override
	public KeyDescriptor<String> getKeyDescriptor() {
		return EnumeratorStringDescriptor.INSTANCE;
	}

	@NotNull
	@Override
	public DataExternalizer<String> getValueExternalizer() {
		return new MyDataExternalizer();
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@NotNull
	@Override
	public InputFilter getInputFilter() {
		return new DefaultFileTypeSpecificInputFilter(JavaFileType.INSTANCE);
	}

	@Override
	public boolean dependsOnFileContent() {
		return false;
	}

	private static class MyDataIndexer implements DataIndexer<String, String, FileContent> {
		@NotNull
		@Override
		public Map<String, String> map(@NotNull final FileContent inputData) {
			final PsiDependentFileContent content = (PsiDependentFileContent) inputData;
			if (inputData.getFile().getName().equals("BlockGeneratorPlugin.java")) {
				System.out.println(inputData.getClass().getName());
				System.out.println(content.getContent());
			}
			return new HashMap<>(Map.ofEntries(Map.entry(inputData.getFile().getName(), inputData.getFileName())));
		}
	}

	private static class MyDataExternalizer implements DataExternalizer<String> {
		@Override
		public void save(@NotNull final DataOutput out, final String value) throws IOException {
			out.writeUTF(value);
		}

		@NotNull
		@Override
		public String read(@NotNull final DataInput in) throws IOException {
			return in.readUTF();
		}
	}
}
