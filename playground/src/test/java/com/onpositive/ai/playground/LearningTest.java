package com.onpositive.ai.playground;

import java.util.Iterator;
import java.util.function.Supplier;

import org.junit.Test;

import com.mulesoft.nn.api.SimpleDataSource;
import com.mulesoft.nn.blocks.CompleteModel;
import com.mulesoft.nn.blocks.ModelElement;
import com.mulesoft.nn.config.ActivationFunction;
import com.mulesoft.nn.config.LearningAlgoritm;
import com.mulesoft.nn.config.LearningScheme;
import com.mulesoft.nn.config.LossFunction;
import com.mulesoft.nn.workspace.impl.ExperimentListener;
import com.mulesoft.nn.workspace.impl.PreparedDataSetArea;
import com.mulesoft.nn.workspace.impl.Workspace;
import com.onpositive.ai.playground.ai.GameSamplesProvider;
import com.onpositive.ai.playground.ai.GameStateDTO;

import junit.framework.TestCase;

public class LearningTest extends TestCase{
	@Test
	public void test1() throws Exception {
		PreparedDataSetArea<GameStateDTO> prepared = Workspace.getDefault().prepare(SimpleDataSource.empty(GameStateDTO.class), true);
		GameSamplesProvider provider = new GameSamplesProvider();
		int iteration = 0;
		int goodCount = 0;
		prepared.withGenerator(new Supplier<GameStateDTO>() {
			
			Iterator<GameStateDTO> iterator;
			
			@Override
			public GameStateDTO get() {
				if (iterator == null || !iterator.hasNext()) {
					iterator = provider.provideNextBatch().iterator(); 
				}
				return iterator.next();
			}
		});
		
		ModelElement input = prepared.input("obstacles").concat(prepared.input("unitAction").appendAxis()).concat(prepared.input("units"));
		System.out.println(input.getOutputShape());
		CompleteModel mdl = input.conv2d(200, 2, ActivationFunction.RELU).conv2d(200, 2, ActivationFunction.RELU).maxPool2D(2).conv2d(200, 2, ActivationFunction.RELU).flatten().concat(prepared.input("action"))
				.dense(ActivationFunction.RELU, 400).outBlock(ActivationFunction.NONE, LossFunction.MEAN_SQUARE_LOSS)
				.toModel();
		prepared.setIterationsPerEpoch(1000000);
		prepared.add(new ExperimentListener<GameStateDTO>() {

			public void started(String name, com.mulesoft.nn.api.ITrainableModel<GameStateDTO> model) {
//				Episode.model = model;
			}

		});
		LearningScheme sc = new LearningScheme(LearningAlgoritm.SGD, 10);
		sc.setLearningRate(0.01);
		prepared.experiment("1", mdl, sc);

	}
	
//	public void test2() {
//		GameSamplesProvider samplesProvider = new GameSamplesProvider();
//		EnvironmentDataSet<GameStateDTO> preparedDataSet = new EnvironmentDataSet<GameStateDTO>(new PreparedDataSetConfig<GameStateDTO>(new SimpleDataSource<GameStateDTO>("game", new LearningIterable<GameStateDTO>(samplesProvider), GameStateDTO.class), null,5), 1000, 100);
//		Input obstacleInput = new Input(preparedDataSet, "obstacles");
//		Input actionInput = new Input(preparedDataSet, "unitAction");
////		Input unitsInput = new Input(preparedDataSet, "units");
////		new Input(preparedDataSet,"obstacles", "unitAction", "units")
//		CompleteModel model = obstacleInput.flatten().concat(actionInput).dense(10, ActivationFunction.RELU)
//				.dense(1, ActivationFunction.SIGMOID).outBlock(ActivationFunction.RELU).toModel();
//		ModelExecutor.getDefault().execute(f -> {
//			ModelSetup<GameStateDTO> setup = preparedDataSet.createSetup(model, LearningAlgoritm.SGD, 0.001);
//			ITrainableModel<GameStateDTO> createdModel = f.create(setup);
//			samplesProvider.setModel(createdModel);
//			for (int j = 0; j < 3; j++) {
//				IHistory fit = createdModel.fit(preparedDataSet, 2, 10);
////				String js = new Experiment<GameStateDTO>("i", preparedDataSet, setup, (HistoryDTO) fit).toJSON();
////				new Experiment<GameStateDTO>(preparedDataSet, Utils.fromJSON(js, ExperimentDTO.class));
////				System.out.println(preparedDataSet.testSet().iterator().next().likesGirls);
////				Collection<GameStateDTO> copy = Utils.copy(preparedDataSet.testSet());
////				System.out.println(copy.iterator().next().likesGirls);
////				create.predict(copy);
////				System.out.println(copy.iterator().next().likesGirls);
////				System.out.println(fit.last());
//			}
//		});
//	}

}
