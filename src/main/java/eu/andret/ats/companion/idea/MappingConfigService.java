package eu.andret.ats.companion.idea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.XCollection;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service(Service.Level.PROJECT)
@State(name = "MappingConfig", storages = @Storage("ats.xml"))
public final class MappingConfigService implements PersistentStateComponent<MappingConfigService> {
	@XCollection
	private final List<String> argumentMappers = new ArrayList<>();
	@XCollection
	private final List<String> argumentCompleters = new ArrayList<>();
	@XCollection
	private final List<Class<?>> typeMappers = new ArrayList<>();
	@XCollection
	private final List<Class<?>> typeCompleters = new ArrayList<>();

	@NotNull
	@Override
	public MappingConfigService getState() {
		return this;
	}

	@Override
	public void loadState(@NotNull final MappingConfigService state) {
		XmlSerializerUtil.copyBean(state, this);
	}
}
