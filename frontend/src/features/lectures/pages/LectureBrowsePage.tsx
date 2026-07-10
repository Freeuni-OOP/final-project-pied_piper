// Browse page listing lectures from the university syllabus with pagination and filters.
  useEffect(() => {
    const load = async () => {
      setError(null);
      setLoading(true);
      try {
        const data = await listFaculties();
        setFaculties(data);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load faculties.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  // Load semesters when faculty is selected
  useEffect(() => {
    if (!selectedFaculty) return;

    const load = async () => {
      setError(null);
      try {
        const data = await listSemesters(selectedFaculty);
        setSemesters(data);
        setSelectedSemester(null);
        setSubjects([]);
        setSelectedSubject(null);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load semesters.');
      }
    };
    load();
  }, [selectedFaculty]);

  // Load subjects when semester is selected
  useEffect(() => {
    if (!selectedSemester) return;

    const load = async () => {
      setError(null);
      try {
        const data = await listSubjects(selectedSemester);
        setSubjects(data);
        setSelectedSubject(null);
      } catch (err: any) {
        setError(err?.message ?? 'Unable to load subjects.');
      }
    };
    load();
  }, [selectedSemester]);

  // Load subject syllabus when subject is selected
  useEffect(() => {
    if (!selectedSubject && subjects.length > 0) return;

    if (selectedSubject?.id) {
      const load = async () => {
        setError(null);
        try {
          const data = await getSubjectSyllabus(selectedSubject.id);
          setSelectedSubject(data);
        } catch (err: any) {
          setError(err?.message ?? 'Unable to load syllabus.');
        }
      };
      load();
    }
  }, [selectedSubject?.id]);

  const handleSubjectClick = (subject: Subject) => {
    setSelectedSubject({ ...subject, lectures: [] } as SubjectSyllabus);
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-gray-900">Browse Lectures</h1>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg mb-6">
          {error}
        </div>
      )}

      {loading && faculties.length === 0 && (
        <p className="text-gray-500">Loading faculties…</p>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Faculty Selection */}
        <div className="lg:col-span-1">
          <h3 className="text-lg font-semibold mb-3 text-gray-900">Faculties</h3>
          <div className="space-y-2">
            {faculties.map((faculty) => (
              <button
                key={faculty.id}
                onClick={() => setSelectedFaculty(faculty.id)}
                className={`w-full text-left px-4 py-2 rounded-lg border transition-colors ${
                  selectedFaculty === faculty.id
                    ? 'bg-blue-100 border-blue-300 font-medium'
                    : 'bg-white border-gray-200 hover:bg-gray-50'
                }`}
              >
                {faculty.name}
              </button>
            ))}
          </div>
        </div>

        {/* Semesters & Subjects */}
        <div className="lg:col-span-3">
          {selectedFaculty && (
            <>
              <div className="mb-6">
                <h3 className="text-lg font-semibold mb-3 text-gray-900">Semesters</h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                  {semesters.map((semester) => (
                    <button
                      key={semester.id}
                      onClick={() => setSelectedSemester(semester.id)}
                      className={`px-3 py-2 rounded-lg border transition-colors text-sm ${
                        selectedSemester === semester.id
                          ? 'bg-blue-100 border-blue-300 font-medium'
                          : 'bg-white border-gray-200 hover:bg-gray-50'
                      }`}
                    >
                      {semester.name}
                    </button>
                  ))}
                </div>
              </div>

              {selectedSemester && (
                <div className="mb-6">
                  <h3 className="text-lg font-semibold mb-3 text-gray-900">Subjects</h3>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {subjects.map((subject) => (
                      <button
                        key={subject.id}
                        onClick={() => handleSubjectClick(subject)}
                        className={`text-left px-4 py-3 rounded-lg border transition-colors ${
                          selectedSubject?.id === subject.id
                            ? 'bg-blue-100 border-blue-300'
                            : 'bg-white border-gray-200 hover:bg-gray-50'
                        }`}
                      >
                        <p className="font-medium text-gray-900">{subject.name}</p>
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {selectedSubject && selectedSubject.lectures && selectedSubject.lectures.length > 0 && (
                <div>
                  <h3 className="text-lg font-semibold mb-3 text-gray-900">{selectedSubject.name} - Lectures</h3>
                  <div className="space-y-2">
                    {selectedSubject.lectures.map((lecture) => (
                      <Link
                        key={lecture.id}
                        to={`/lectures/${lecture.id}`}
                        className="block px-4 py-3 bg-white border border-gray-200 rounded-lg hover:shadow-md transition-shadow"
                      >
                        <h4 className="font-medium text-gray-900">
                          {lecture.title} (Week {lecture.week}, Lecture {lecture.lectureNumber})
                        </h4>
                        {lecture.description && (
                          <p className="text-sm text-gray-600 mt-1">{lecture.description}</p>
                        )}
                      </Link>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

