import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.feature.VectorAssembler

val dataframe = spark.read.option("header", "true").option("inferSchema", "true").format("csv").load("Documents/big data/linux_data/fermentation.csv")
val selectedSubset =  dataframe.select("Glucose concentration","Acetate concentration","Ethanol concentration","Specific oxygen uptake rate", "Specific carbon dioxide evolution rate", "Biomass")
val vectorAssembler = new VectorAssembler().setInputCols(Array("Glucose concentration","Acetate concentration","Ethanol concentration","Specific oxygen uptake rate", "Specific carbon dioxide evolution rate")).setOutputCol("features")
val transformedData = vectorAssembler.transform(selectedSubset).select("features", "Biomass")
val Array(train, test) = transformedData.withColumnRenamed("Biomass", "label").randomSplit(Array(0.7, 0.3), seed=21)
val linearRegression = new LinearRegression()
val regModel = linearRegression.fit(train)
val predictions = regModel.transform(test)
predictions.select("prediction", "label", "features").show(10)
val rootMeanSquaredError = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("rmse").evaluate(predictions)
println(s"Root Mean Squared Error (RMSE) on test data = $rootMeanSquaredError")
println(s"participants: 1. Markus Aleksander Råkil Johansen 2. Frederik Andreas Brunvoll Farstad")