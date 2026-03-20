const students = [
  {
    name: "Lalit",
    marks: [
      { subject: "Math", score: 78 },
      { subject: "English", score: 82 },
      { subject: "Science", score: 74 },
      { subject: "History", score: 69 },
      { subject: "Computer", score: 88 },
    ],
    attendance: 82,
  },
  {
    name: "Rahul",
    marks: [
      { subject: "Math", score: 90 },
      { subject: "English", score: 85 },
      { subject: "Science", score: 80 },
      { subject: "History", score: 76 },
      { subject: "Computer", score: 92 },
    ],
    attendance: 91,
  },
  {
    name: "Aman",
    marks: [
      { subject: "Math", score: 55 },
      { subject: "English", score: 60 },
      { subject: "Science", score: 38 },
      { subject: "History", score: 50 },
      { subject: "Computer", score: 62 },
    ],
    attendance: 80,
  },
  {
    name: "Riya",
    marks: [
      { subject: "Math", score: 72 },
      { subject: "English", score: 68 },
      { subject: "Science", score: 75 },
      { subject: "History", score: 70 },
      { subject: "Computer", score: 80 },
    ],
    attendance: 70,
  },
];

// ---------------- TOTAL MARKS ----------------
function calculateTotal(student) {
  let total = 0;
  for (let i = 0; i < student.marks.length; i++) {
    total = total + student.marks[i].score;
  }
  return total;
}

console.log("===== TOTAL MARKS =====");
for (let i = 0; i < students.length; i++) {
  console.log(
    students[i].name + " Total Marks: " + calculateTotal(students[i]),
  );
}

// ---------------- AVERAGE ----------------
function calculateAverage(student) {
  let total = calculateTotal(student);
  let avg = total / student.marks.length;
  return parseFloat(avg.toFixed(1));
}

console.log("===== AVERAGE MARKS =====");
for (let i = 0; i < students.length; i++) {
  console.log(students[i].name + " Average: " + calculateAverage(students[i]));
}

// ---------------- SUBJECT HIGHEST ----------------
console.log("===== SUBJECT-WISE HIGHEST SCORE =====");

var subjectNames = ["Math", "English", "Science", "History", "Computer"];

for (let s = 0; s < subjectNames.length; s++) {
  let subjectName = subjectNames[s];
  let highest = -1;
  let topperName = "";

  for (let i = 0; i < students.length; i++) {
    for (let j = 0; j < students[i].marks.length; j++) {
      if (students[i].marks[j].subject === subjectName) {
        if (students[i].marks[j].score > highest) {
          highest = students[i].marks[j].score;
          topperName = students[i].name;
        }
      }
    }
  }

  console.log(
    "Highest in " + subjectName + ": " + topperName + " (" + highest + ")",
  );
}

// ---------------- SUBJECT AVERAGE ----------------
console.log("===== SUBJECT-WISE AVERAGE SCORE =====");

var subjectNames = ["Math", "English", "Science", "History", "Computer"];

for (let s = 0; s < subjectNames.length; s++) {
  let subjectName = subjectNames[s];
  let total = 0;

  for (let i = 0; i < students.length; i++) {
    for (let j = 0; j < students[i].marks.length; j++) {
      if (students[i].marks[j].subject === subjectName) {
        total = total + students[i].marks[j].score;
      }
    }
  }

  let avg = parseFloat((total / students.length).toFixed(1));
  console.log("Average " + subjectName + " Score: " + avg);
}

// ---------------- TOPPER ----------------
console.log("===== CLASS TOPPER =====");

let highestTotal = -1;
let topperName = "";

for (let i = 0; i < students.length; i++) {
  let total = calculateTotal(students[i]);
  if (total > highestTotal) {
    highestTotal = total;
    topperName = students[i].name;
  }
}

console.log("Class Topper: " + topperName + " with " + highestTotal + " marks");

// ---------------- GRADE ----------------
function assignGrade(student) {
  let avg = calculateAverage(student);

  // fail condition 1 - low attendance
  if (student.attendance < 75) {
    return "Fail (Low Attendance)";
  }

  // fail condition 2 - any subject score 40 or below
  let failedSubject = "";
  for (let i = 0; i < student.marks.length; i++) {
    if (student.marks[i].score <= 40) {
      failedSubject = student.marks[i].subject;
    }
  }
  if (failedSubject !== "") {
    return "Fail (Failed in " + failedSubject + ")";
  }

  // normal grade
  if (avg >= 85) {
    return "A";
  } else if (avg >= 70) {
    return "B";
  } else if (avg >= 50) {
    return "C";
  } else {
    return "Fail";
  }
}

console.log("===== GRADES =====");
for (let i = 0; i < students.length; i++) {
  console.log(students[i].name + " Grade: " + assignGrade(students[i]));
}
