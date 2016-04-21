namespace java fr.cnrs.liris.accio.core.thrift

include "op.thrift"

// A metric output as captured by the evaluation tool.
struct Metric {
  // Full metric name (should include the evaluator name).
  1: required string name;

  // Value generated by the metric.
  2: required double value;
}

struct ReportItem {
  1: required list<Metric> analyses;
  2: required list<Metric> evaluations;
  3: required i32 solution_index;
  4: required i32 user_index;
}

struct Solution {
  // Solution definition.
  1: required op.Transformation transformation;

  // Cost of the solution, as evaluated by the optimization process.
  3: optional list<double> optimization_cost;

  // Time spent optimizing (clock time).
  4: optional list<i64> optimization_duration;
}

struct Report {
  // Time at which this report was generated.
  1: required i64 wall_time;

  // Name of the treatment being evaluated.
  2: required string name;

  // List of solutions items can refer to.
  3: required list<Solution> solutions;

  // List of users items can refer to.
  4: required list<string> users;

  // Items containing actual results.
  6: required list<ReportItem> items;
}